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
        log.info("Buscando álbumes por nombre: {} y artista: {}", nombre, artista);

        if ((nombre == null || nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findAll());
        }
        if ((nombre != null && !nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findAllByNombre(nombre));
        }
        if ((nombre == null || nombre.isEmpty()) && (artista != null && !artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findAllByArtista(artista));
        }
        return albumMapper.toResponseDtoList(albumRepository.findAllByNombreAndArtista(nombre, artista));
    }

    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando álbum por id {}", id);
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id)));
    }

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

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto) {
        log.info("Guardando álbum: {}", createDto);
        Long id = albumRepository.nextId();
        Album nuevoAlbum = albumMapper.toAlbum(id, createDto);
        return albumMapper.toAlbumResponseDto(albumRepository.save(nuevoAlbum));
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        var albumActual = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
        Album albumActualizado = albumMapper.toAlbum(updateDto, albumActual);
        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum por id: {}", id);
        albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
        albumRepository.deleteById(id);
    }
}