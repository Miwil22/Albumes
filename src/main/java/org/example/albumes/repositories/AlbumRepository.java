package org.example.albumes.repositories;

import org.example.albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// @Repository: Indica a Spring que esta interfaz es un componente de acceso a datos (DAO).
// extends JpaRepository<Album, Long>: ¡Magia de Spring Data!
// Al heredar de esto, obtenemos automáticamente métodos para guardar, borrar y buscar (findAll, save, findById, delete)
// para la entidad 'Album' cuyo ID es de tipo 'Long'. No hace falta implementarlos.
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // CONSULTA DERIVADA (Derived Query):
    // Spring analiza el nombre del método y genera el SQL automáticamente.
    // "findByNombre" -> Busca por columna nombre.
    // "Containing" -> Usa el operador LIKE %valor% (busca coincidencias parciales).
    // "IgnoreCase" -> Ignora mayúsculas y minúsculas al comparar.
    // SQL aprox: SELECT * FROM albumes WHERE UPPER(nombre) LIKE UPPER('%parametro%')
    List<Album> findByNombreContainingIgnoreCase(String nombre);

    // CONSULTA JPQL (@Query):
    // Aquí escribimos la consulta manualmente usando el lenguaje de consulta de objetos de Java (JPQL).
    // No usamos nombres de tablas (ALBUMES), sino nombres de Clases (Album).
    // "a.artista.nombre": Navegamos desde el objeto Album (a) a su propiedad 'artista' y de ahí a 'nombre'.
    // LOWER(CONCAT('%', :artista, '%')): Convierte todo a minúsculas y añade los % para buscar "contiene".
    // :artista hace referencia al parámetro del método String artista.
    @Query("SELECT a FROM Album a WHERE LOWER(a.artista.nombre) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Album> findByArtistaNombreContainingIgnoreCase(String artista);

    // Consulta JPQL más compleja con dos condiciones (AND).
    // Busca álbumes donde el nombre coincida Y el artista coincida.
    @Query("SELECT a FROM Album a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND LOWER(a.artista" +
            ".nombre) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Album> findByNombreAndArtista(String nombre, String artista);

    // Métodos que faltaban en la interfaz estándar de JpaRepository y añadimos manualmente.
    // Busca un álbum por su campo UUID. Devuelve Optional para evitar nulos si no existe.
    Optional<Album> findByUuid(UUID uuid);

    // Comprueba si existe un álbum con ese UUID. Devuelve true o false. (SQL: SELECT COUNT(*) > 0 ...)
    boolean existsByUuid(UUID uuid);

    // Borra un álbum buscando por su UUID.
    void deleteByUuid(UUID uuid);

    // Método para buscar solo los álbumes que NO han sido borrados lógicamente (isDeleted = false).
    List<Album> findByIsDeleted(Boolean isDeleted);

    // CONSULTA DE MODIFICACIÓN (UPDATE):
    // @Modifying: Obligatorio cuando la consulta @Query no es un SELECT, sino un UPDATE o DELETE.
    // Esta query implementa el Borrado Lógico: actualiza el campo isDeleted a true para el ID dado.
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}