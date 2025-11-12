package albumes.repositories;

import albumes.models.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumRepositoryImplTest {

    private AlbumRepositoryImpl repositorio;
    private final UUID testUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repositorio = new AlbumRepositoryImpl();


        Album album1 = repositorio.findById(1L).get();
        album1.setUuid(testUuid);
        repositorio.save(album1);
    }

    @Test
    void findAll() {
        List<Album> albumes = repositorio.findAll();
        assertAll("findAll",
                () -> assertNotNull(albumes),
                () -> assertEquals(2, albumes.size())
        );
    }

    @Test
    void findAllByNombre() {
        List<Album> albumes = repositorio.findAllByNombre("Abbey Road");
        assertAll("findAllByNombre",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals("Abbey Road", albumes.getFirst().getNombre())
        );
    }

    @Test
    void findAllByArtista() {
        List<Album> albumes = repositorio.findAllByArtista("Michael");
        assertAll("findAllByArtista",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals("Thriller", albumes.getFirst().getNombre())
        );
    }

    @Test
    void findAllByNombreAndArtista() {
        List<Album> albumes = repositorio.findAllByNombreAndArtista("Abbey", "Beatles");
        assertEquals(1, albumes.size());
    }

    @Test
    void findById_existingId_returnsOptionalWithAlbum() {
        Long id = 1L;
        Optional<Album> optAlbum = repositorio.findById(id);

        assertAll("findById_existingId",
                () -> assertNotNull(optAlbum),
                () -> assertTrue(optAlbum.isPresent()),
                () -> assertEquals(id, optAlbum.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        Long id = 99L;
        Optional<Album> optAlbum = repositorio.findById(id);
        assertTrue(optAlbum.isEmpty());
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithAlbum() {
        Optional<Album> optAlbum = repositorio.findByUuid(testUuid);
        assertTrue(optAlbum.isPresent());
        assertEquals(testUuid, optAlbum.get().getUuid());
    }

    @Test
    void existsById_existingId_returnsTrue() {
        assertTrue(repositorio.existsById(1L));
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        assertFalse(repositorio.existsById(99L));
    }

    @Test
    void save_notExists() {
        Album album3 = Album.builder()
                .id(3L)
                .nombre("New Album")
                .artista("New Artist")
                .build();

        Album savedAlbum = repositorio.save(album3);
        var all = repositorio.findAll();

        assertAll("save new",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album3, savedAlbum),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void save_butExists_updates() {
        Album albumUpdate = Album.builder()
                .id(1L)
                .nombre("UPDATED") // Nuevo nombre
                .artista("The Beatles")
                .build();

        Album savedAlbum = repositorio.save(albumUpdate);
        var all = repositorio.findAll();
        var found = repositorio.findById(1L);

        assertAll("save update",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(albumUpdate, savedAlbum),
                () -> assertEquals(2, all.size()),
                () -> assertEquals("UPDATED", found.get().getNombre())
        );
    }

    @Test
    void deleteById_existingId() {
        Long id = 1L;
        repositorio.deleteById(id);
        var all = repositorio.findAll();

        assertAll("deleteById_existingId",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repositorio.existsById(id))
        );
    }

    @Test
    void nextId() {
        Long nextId = repositorio.nextId();
        assertEquals(3L, nextId);
    }
}