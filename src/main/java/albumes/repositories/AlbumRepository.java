package albumes.repositories;

import albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Por nombre (Equivalente a findByNumero pero usando Containing para búsquedas parciales)
    // En Tarjetas es findByNumero (exacto), aquí usamos Containing para nombres de álbumes
    List<Album> findByNombreContainingIgnoreCase(String nombre);

    // Por artista (Equivalente a findByTitularContainsIgnoreCase)
    // Usamos JPQL para navegar a la entidad Artista
    @Query("SELECT a FROM Album a WHERE LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByArtistaNombreContainingIgnoreCase(String artista);

    // Por nombre y artista (Equivalente a findByNumeroAndTitularContainsIgnoreCase)
    @Query("SELECT a FROM Album a WHERE LOWER(a.nombre) LIKE %:nombre% AND LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByNombreContainingIgnoreCaseAndArtistaNombreContainingIgnoreCase(String nombre, String artista);

    // Por UUID
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Si está borrado (Equivalente a findByIsDeleted)
    List<Album> findByIsDeleted(Boolean isDeleted);

    // Actualizar el álbum con isDeleted a true (Borrado lógico)
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}