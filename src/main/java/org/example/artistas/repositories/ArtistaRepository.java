package org.example.artistas.repositories;

import org.example.artistas.models.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    // Busca un artista por nombre EXACTO (ignorando mayúsculas).
    // Útil para comprobar duplicados antes de guardar.
    Optional<Artista> findByNombreEqualsIgnoreCase(String nombre);

    // Busca artistas cuyo nombre CONTENGA el texto (ej: "a" -> "Ana", "Paco").
    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    // CONSULTA COMPLEJA (@Query):
    // Aquí cruzamos fronteras. Estamos en ArtistaRepository, pero preguntamos por Álbumes.
    // "Cuenta los álbumes (COUNT) que tengan como artista el ID que te paso".
    // Si el conteo > 0 devuelve TRUE (no se puede borrar). Si no, FALSE (se puede borrar).
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.id = :id")
    Boolean existsAlbumById(Long id);
}