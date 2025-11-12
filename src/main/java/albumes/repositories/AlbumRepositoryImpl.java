package albumes.repositories;

import albumes.models.Album;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class AlbumRepositoryImpl implements AlbumRepository {
    private final Map<Long, Album> albumes = new LinkedHashMap<>(
            Map.of(
                    1L, Album.builder()
                            .id(1L)
                            .nombre("Abbey Road")
                            .artista("The Beatles")
                            .genero("Rock")
                            .precio(19.99f)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
                    2L,  Album.builder()
                            .id(2L)
                            .nombre("Thriller")
                            .artista("Michael Jackson")
                            .genero("Pop")
                            .precio(15.29f)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build()
            ));


    @Override
    public List<Album> findAll() {
        log.info("Buscando álbumes");
        return albumes.values().stream()
                .toList();
    }

    @Override
    public List<Album> findAllByNombre(String nombre) {
        log.info("Buscando álbumes por nombre: {}", nombre);
        return albumes.values().stream()
                .filter(album -> album.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<Album> findAllByArtista(String artista) {
        log.info("Buscando álbumes por artista: {}", artista);
        return albumes.values().stream()
                .filter(album -> album.getArtista().toLowerCase().contains(artista.toLowerCase()))
                .toList();
    }

    @Override
    public List<Album> findAllByNombreAndArtista(String nombre, String artista) {
        log.info("Buscando álbumes por nombre: {} y artista: {} ", nombre, artista);
        return albumes.values().stream()
                .filter(album -> album.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(album -> album.getArtista().toLowerCase().contains(artista.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Album> findById(Long id) {
        log.info("Buscando álbum por id: {}", id);
        return albumes.get(id) != null ? Optional.of(albumes.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Album> findByUuid(UUID uuid) {
        log.info("Buscando álbum por uuid: {}", uuid);
        return albumes.values().stream()
                .filter(album -> album.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Comprobando si existe álbum por id: {}", id);
        return albumes.get(id) != null;
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        log.info("Comprobando si existe álbum por uuid: {}", uuid);
        return albumes.values().stream()
                .anyMatch(album -> album.getUuid().equals(uuid));
    }

    @Override
    public Album save(Album album) {
        log.info("Guardando álbum: {}", album);
        albumes.put(album.getId(), album);
        return album;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Borrando álbum por id: {}", id);
        albumes.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        log.info("Borrando álbum por uuid: {}", uuid);
        albumes.values().removeIf(album -> album.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id de álbum");
        return albumes.keySet().stream()
                .mapToLong(value -> value)
                .max()
                .orElse(0) + 1;
    }
}