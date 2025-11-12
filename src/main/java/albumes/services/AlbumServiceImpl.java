package albumes.services;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.exceptions.AlbumBadUuidException;
import albumes.exceptions.AlbumNotFoundException;
import albumes.mappers.AlbumMapper;
import albumes.models.Album;
import albumes.repositories.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"albumes"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Override
    public List<AlbumResponseDto> findAll(String nombre, String artista) {
        // Si todos los args están vacíos o nulos, devolvemos todos los álbumes
        if ((nombre == null || nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            log.info("Buscando todos los álbumes");
            return albumMapper.toResponseDtoList(albumRepository.findAll());
        }
        // Si el nombre no está vacío, pero el artista si, buscamos por nombre
        if ((nombre != null && !nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            log.info("Buscando álbumes por nombre: {}", nombre);
            return albumMapper.toResponseDtoList(albumRepository.findAllByNombre(nombre));
        }
        // Si el nombre está vacío, pero el artista no, buscamos por artista
        if (nombre == null || nombre.isEmpty()) {
            log.info("Buscando álbumes por artista: {}", artista);
            return albumMapper.toResponseDtoList(albumRepository.findAllByArtista(artista));
        }
        // Si el nombre y el artista no están vacíos, buscamos por ambos
        log.info("Buscando álbumes por nombre: {} y artista: {}", nombre, artista);
        return albumMapper.toResponseDtoList(albumRepository.findAllByNombreAndArtista(nombre, artista));
    }

    // Cachea con el id como key
    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando álbum por id {}", id);

        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(()-> new AlbumNotFoundException(id)));
    }

    // Cachea con el uuid como key
    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        log.info("Buscando álbum por uuid: {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlbumNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new AlbumBadUuidException(uuid);
        }

    }

    // Cachea con el id del resultado de la operación como key
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto) {
        log.info("Guardando álbum: {}", createDto);
        // obtenemos el id de álbum
        Long id = albumRepository.nextId();
        // Creamos el álbum nuevo con los datos que nos vienen
        Album nuevoAlbum = albumMapper.toAlbum(id, createDto);
        // Lo guardamos en el repositorio
        return albumMapper.toAlbumResponseDto(albumRepository.save(nuevoAlbum));
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        // Si no existe lanza excepción
        var albumActual = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        // Actualizamos el álbum con los datos que nos vienen
        Album albumActualizado =  albumMapper.toAlbum(updateDto, albumActual);
        // Lo guardamos en el repositorio
        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    // El key es opcional, si no se indica
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum por id: {}", id);
        // Si no existe lanza excepción
        albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        // Lo borramos del repositorio si existe
        albumRepository.deleteById(id);

    }

}