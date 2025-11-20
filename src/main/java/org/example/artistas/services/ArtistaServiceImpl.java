package org.example.artistas.services;


import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service // Marca la clase como lógica de negocio
@CacheConfig(cacheNames = "{artista}") // Todo se cachea bajo el nombre "artista"
public class ArtistaServiceImpl implements ArtistaService{

    // Inyectamos repositorio (BD) y mapper (Traductor)
    private final ArtistaRepository artistaRepository;
    private final ArtistaMapper artistaMapper;

    @Override
    public List<Artista> findAll(String nombre) {
        log.info("Buscando artistas por nombre: {}", nombre);
        // Si no hay filtro, devuelve todos. Si hay, busca parecidos.
        if (nombre == null || nombre.isEmpty()){
            return artistaRepository.findAll();
        } else {
            return artistaRepository.findByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    public Artista findByNombre(String nombre) {
        log.info("Buscando artista por nombre: {}", nombre);
        // Busca exacto. Si no está -> Error 404.
        return artistaRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new ArtistaNotFoundException(nombre));
    }

    @Override
    @Cacheable // Guarda el resultado en RAM para ir rápido la próxima vez.
    public Artista findById(Long id) {
        log.info("Buscando artista por id:{}", id);
        return artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNotFoundException(id));
    }

    @Override
    @CachePut // Actualiza la caché con el nuevo dato.
    public Artista save(ArtistaRequestDto artistaRequestDto) {
        log.info("Guardando artista: {}", artistaRequestDto);

        // REGLA DE NEGOCIO: No puede haber dos artistas con el mismo nombre.
        // Buscamos si ya existe uno igual. Si sí -> Error 409 Conflict.
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
        });

        // Si no existe, lo convertimos y guardamos.
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto));
    }

    @Override
    @CachePut // Actualiza la caché.
    public Artista update(Long id, ArtistaRequestDto artistaRequestDto) {
        log.info("Actualizando artista: {}", artistaRequestDto);

        // 1. Buscamos si el artista a editar existe.
        Artista artistaActual = findById(id);

        // 2. Si nos están cambiando el nombre, verificamos que el nuevo nombre no pertenezca YA a otro artista distinto.
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            if (!art.getId().equals(id)){ // Si el ID no es el mío, es que el nombre está cogido.
                throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
            }
        });

        // 3. Guardamos la actualización.
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto, artistaActual));
    }

    @Override
    @CacheEvict // Borra de la caché para no mostrar datos fantasmas.
    @Transactional // Asegura la operación.
    public void deleteById(Long id) {
        log.info("Borrando artista por id: {}", id);
        // 1. Verificamos si existe el artista.
        findById(id);

        // 2. REGLA DE INTEGRIDAD: No puedes borrar un padre si tiene hijos (álbumes) colgando.
        // Usamos la consulta personalizada que hicimos en el repositorio.
        if (artistaRepository.existsAlbumById(id)){
            String mensaje = "No se puede borrar el artista con id: " + id + " porque tiene álbumes asociados";
            log.warn(mensaje);
            throw new ArtistaConflictException(mensaje); // Error 409 Conflict.
        }else {
            // Si no tiene hijos, lo borramos sin piedad.
            artistaRepository.deleteById(id);
        }

    }
}