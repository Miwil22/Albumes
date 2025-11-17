package albumes.repositories;

import albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByNombreContainingIgnoreCase(String nombre);

    // Buscamos por el nombre del artista asociado
    @Query("SELECT a FROM Album a WHERE LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByArtistaNombreContainingIgnoreCase(String artista);

    @Query("SELECT a FROM Album a WHERE LOWER(a.nombre) LIKE %:nombre% AND LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByNombreAndArtista(String nombre, String artista);

    Optional<Album> findByUuid(UUID uuid);
}