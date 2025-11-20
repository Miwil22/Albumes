package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadUuidException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.services.ArtistaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// @CacheConfig: Configuración general de caché para esta clase. Todos los métodos usarán la caché llamada "albumes".
@CacheConfig(cacheNames = {"albumes"})
// @Slf4j: Nos da un objeto 'log' para escribir mensajes en la consola (útil para depurar).
@Slf4j
// @RequiredArgsConstructor: Crea un constructor con todos los campos 'final'.
// Esto permite que Spring inyecte automáticamente el repositorio, el mapper y el servicio de artistas.
@RequiredArgsConstructor
// @Service: Marca esta clase como un componente de Servicio de Spring (Lógica de Negocio).
@Service
public class AlbumServiceImpl implements AlbumService {

    // Inyección de dependencias: Necesitamos estas herramientas para trabajar.
    private final AlbumRepository albumRepository; // Para acceder a la BD.
    private final AlbumMapper albumMapper;         // Para convertir datos.
    private final ArtistaService artistaService;   // Para buscar artistas (validación cruzada).

    @Override
    public List<AlbumResponseDto> findAll(String nombre, String artista) {
        // Lógica de filtrado: decidimos qué método del repositorio llamar según los parámetros recibidos.

        // Caso 1: No hay filtros (ambos son nulos o vacíos). Devolvemos todo.
        if ((nombre == null || nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            // .findAll() devuelve entidades Album. Usamos el mapper para convertirlas a DTOs.
            return albumMapper.toResponseDtoList(albumRepository.findAll());
        }
        // Caso 2: Solo hay filtro de nombre. Buscamos por nombre.
        if (nombre != null && (artista == null || artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findByNombreContainingIgnoreCase(nombre));
        }
        // Caso 3: Solo hay filtro de artista. Buscamos por artista.
        if ((nombre == null || nombre.isEmpty()) && artista != null) {
            return albumMapper.toResponseDtoList(albumRepository.findByArtistaNombreContainingIgnoreCase(artista));
        }
        // Caso 4: Hay ambos filtros. Llamamos a la consulta que filtra por los dos (AND).
        // Esta llamada coincide con el @Query que acabamos de poner en el repositorio
        return albumMapper.toResponseDtoList(albumRepository.findByNombreAndArtista(nombre, artista));
    }

    // @Cacheable: Antes de ejecutar el método, Spring mira si ya tiene guardado el resultado para este 'id'.
    // Si lo tiene, lo devuelve de memoria (caché) y NO ejecuta el código. Si no, ejecuta, guarda en caché y devuelve.
    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando álbum por id {}", id);
        // Buscamos en el repositorio.
        // .orElseThrow: Si el Optional está vacío (no existe), lanzamos nuestra excepción personalizada 404.
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id)));
    }

    // @Cacheable con el UUID como clave. Misma lógica que arriba.
    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        log.info("Buscando álbum por uuid: {}", uuid);
        try {
            // Intentamos convertir el String a UUID. Si el formato es malo, Java lanza IllegalArgumentException.
            var myUUID = UUID.fromString(uuid);
            // Buscamos en BD.
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlbumNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            // Capturamos el error de formato y lanzamos nuestra excepción personalizada (que dará un 400 Bad Request).
            throw new AlbumBadUuidException(uuid);
        }
    }

    // @CachePut: Este método SIEMPRE se ejecuta. El resultado que devuelve se guarda/actualiza en la caché con la clave 'result.id'.
    // Se usa para mantener la caché actualizada cuando creamos un dato nuevo.
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto) {
        log.info("Guardando álbum: {}", createDto);

        // 1. Buscamos el artista en la base de datos usando el servicio de Artistas.
        // 'createDto.getArtista()' nos da el nombre (String).
        // Si el artista no existe, 'artistaService.findByNombre' lanzará una excepción y el proceso se detendrá aquí.
        var artista = artistaService.findByNombre(createDto.getArtista());

        // 2. Usamos el Mapper para convertir el DTO en una Entidad Album.
        // Le pasamos el objeto 'artista' real que acabamos de recuperar para establecer la relación.
        Album nuevoAlbum = albumMapper.toAlbum(createDto, artista);

        // 3. Guardamos el nuevo álbum en la base de datos y convertimos el resultado a DTO para devolverlo.
        return albumMapper.toAlbumResponseDto(albumRepository.save(nuevoAlbum));
    }

    // @CachePut: Igual que arriba. Actualiza la caché con el álbum modificado.
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        // 1. Buscamos el álbum original. Si no existe, error 404.
        var albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        // 2. Usamos el mapper para actualizar los campos del álbum original con los datos nuevos del DTO.
        // Esto modifica el objeto 'albumActual' en memoria.
        Album albumActualizado = albumMapper.toAlbum(updateDto, albumActual);

        // 3. Guardamos los cambios en la base de datos.
        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    // @CacheEvict: Cuando borramos un dato, es OBLIGATORIO borrarlo también de la caché.
    // Si no, si alguien pide este ID después de borrarlo, ¡la caché se lo devolvería como si existiera!
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum por id: {}", id);
        // Verificamos que el álbum exista antes de intentar borrarlo. Si no, error 404.
        albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
        // Borrado físico (elimina la fila de la tabla).
        albumRepository.deleteById(id);
    }
}