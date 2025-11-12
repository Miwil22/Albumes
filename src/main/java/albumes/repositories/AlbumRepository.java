package albumes.repositories;

import albumes.models.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository {
    List<Album> findAll();

    List<Album> findAllByNombre(String nombre);

    List<Album> findAllByArtista(String artista);

    List<Album> findAllByNombreAndArtista(String nombre, String artista);

    Optional<Album> findById(Long id);

    Optional<Album> findByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUuid(UUID uuid);

    Album save(Album album);

    void deleteById(Long id);

    void deleteByUuid(UUID uuid);

    Long nextId();
}