package albumes.repositories;

import albumes.AlbumesApplication; // <--- Importamos la App principal
import albumes.models.Album;
import albumes.repositories.AlbumRepository;
import artistas.models.Artista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration; // <--- Necesario
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
// ESTA LÍNEA ARREGLA EL ERROR: Le dice a Spring dónde empezar
@ContextConfiguration(classes = AlbumesApplication.class)
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Artista artista;

    @BeforeEach
    void setUp(){
        // Creamos y guardamos el artista primero (necesario por la relación)
        artista = Artista.builder().nombre("The Beatles").build();
        entityManager.persist(artista);

        // Creamos el álbum vinculado al artista
        Album album = Album.builder()
                .nombre("Abbey Road")
                .genero("Rock")
                .precio(19.99f)
                .artista(artista)
                .uuid(UUID.randomUUID())
                .build();
        entityManager.persist(album);
        entityManager.flush(); // Forzamos que se guarden los datos antes del test
    }

    @Test
    void findAll(){
        List<Album> albumes = albumRepository.findAll();
        assertFalse(albumes.isEmpty());
        assertEquals(1, albumes.size());
    }

    @Test
    void findByNombreContainingIgnoreCase(){
        List<Album> albumes = albumRepository.findByNombreContainingIgnoreCase("abbey");
        assertEquals(1, albumes.size());
    }

    @Test
    void findByArtistaNombre(){
        // Asumiendo que implementaste este método en el repositorio como te indiqué
        List<Album> albumes = albumRepository.findByArtistaNombreContainingIgnoreCase("beatles");
        assertEquals(1, albumes.size());
    }
}