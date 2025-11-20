package albumes.repositories;

import org.example.Application; // Importamos la configuración principal
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista;
import org.example.albumes.repositories.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// @DataJpaTest: Esta anotación configura un entorno de prueba SOLO para la capa de datos.
// Arranca una base de datos H2 en memoria y configura Hibernate automáticamente.
@DataJpaTest
// @ContextConfiguration: Le decimos dónde está nuestra clase principal para que sepa qué paquetes escanear.
@ContextConfiguration(classes = Application.class)
// @Sql: Antes de cada test, ejecuta este script SQL para limpiar las tablas y empezar de cero.
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AlbumRepositoryTest {

    // Inyectamos el repositorio que queremos probar.
    @Autowired
    private AlbumRepository albumRepository;

    // TestEntityManager: Es una herramienta de test para meter datos en la BD manualmente sin usar el repositorio.
    // Lo usamos para preparar el escenario ("Arrange").
    @Autowired
    private TestEntityManager entityManager;

    private Artista artista;

    // @BeforeEach: Este método se ejecuta antes de CADA test individual.
    @BeforeEach
    void setUp(){
        // 1. Creamos un artista y lo guardamos en la BD (necesario porque el álbum requiere un artista).
        artista = Artista.builder().nombre("The Beatles").build();
        entityManager.persist(artista);

        // 2. Creamos un álbum vinculado a ese artista.
        Album album = Album.builder()
                .nombre("Abbey Road")
                .genero("Rock")
                .precio(19.99f)
                .artista(artista)
                .uuid(UUID.randomUUID())
                .build();

        // 3. Guardamos el álbum y hacemos 'flush' para obligar a que se escriba en la BD ahora mismo.
        entityManager.persist(album);
        entityManager.flush();
    }

    @Test
    void findAll(){
        // Act: Llamamos al método real del repositorio.
        List<Album> albumes = albumRepository.findAll();

        // Assert: Comprobamos que nos devuelve lo que esperamos (1 álbum que metimos en el setUp).
        assertFalse(albumes.isEmpty()); // La lista no debe estar vacía
        assertEquals(1, albumes.size()); // Debe haber exactamente 1 elemento
    }

    @Test
    void findByNombreContainingIgnoreCase(){
        // Probamos la búsqueda por nombre parcial ("abbey" encuentra "Abbey Road").
        List<Album> albumes = albumRepository.findByNombreContainingIgnoreCase("abbey");
        assertEquals(1, albumes.size());
    }

    @Test
    void findByArtistaNombre(){
        // Probamos la consulta personalizada @Query que busca por nombre de artista.
        List<Album> albumes = albumRepository.findByArtistaNombreContainingIgnoreCase("beatles");
        assertEquals(1, albumes.size());
    }
}