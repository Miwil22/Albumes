package albumes.repositories;

import albumes.models.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumRepositoryImplTest {

    private final Album album1 = Album.builder()
            .id(1L)
            .nombre("Abbey Road")
            .artista("The Beatles")
            .genero("Rock")
            .precio(19.99f)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L)
            .nombre("Thriller")
            .artista("Michael Jackson")
            .genero("Pop")
            .precio(15.29f)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumRepositoryImpl repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new AlbumRepositoryImpl();
        repositorio.save(album1);
        repositorio.save(album2);
    }

    @Test
    void findAll() {
        // Act
        List<Album> albumes = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(albumes),
                () -> assertEquals(2, albumes.size())
        );
    }

    @Test
    void findAllByNombre() {
        // Act
        String nombre = "Thriller";
        List<Album> albumes = repositorio.findAllByNombre(nombre);

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(nombre, albumes.getFirst().getNombre())
        );
    }

    @Test
    void findAllByArtista() {
        // Act
        String artista = "The Beatles";
        List<Album> albumes = repositorio.findAllByArtista(artista);

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(artista, albumes.getFirst().getArtista())
        );
    }

    @Test
    void findAllByNombreAndArtista() {
        // Act
        String nombre = "Thriller";
        String artista = "Michael Jackson";
        List<Album> albumes = repositorio.findAllByNombreAndArtista(nombre, artista);
        // Assert
        assertAll(
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(nombre, albumes.getFirst().getNombre()),
                () -> assertEquals(artista, albumes.getFirst().getArtista())
        );
    }

    @Test
    void findById_existingId_returnsOptionalWithAlbum() {
        // Act
        Long id = 1L;
        Optional<Album> optionalAlbum = repositorio.findById(id);

        // Assert
        assertAll("findById_existingId_returnsOptionalWithAlbum",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(id, optionalAlbum.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // Act
        Long id = 4L;
        Optional<Album> optionalAlbum = repositorio.findById(id);

        // Assert
        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithAlbum() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_existingUuid_returnsOptionalWithAlbum",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(uuid, optionalAlbum.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnsEmptyOptional() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }


    @Test
    void existsById_existingId_returnsTrue() {
        // Act
        Long id = 1L;
        boolean exists = repositorio.existsById(id);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // Act
        Long id = 4L;
        boolean exists = repositorio.existsById(id);

        // Assert
        assertFalse(exists);
    }


    @Test
    void existsByUuid_existingUuid_returnsTrue() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUuid_nonExistingUuid_returnsFalse() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertFalse(exists);
    }

    @Test
    void save_notExists() {
        // Arrange
        Album album = Album.builder()
                .id(3L)
                .nombre("Un Verano Sin Ti")
                .artista("Bad Bunny")
                .precio(25.99f)
                .build();

        // Act
        Album savedAlbum = repositorio.save(album);
        var all = repositorio.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album, savedAlbum),
                () -> assertEquals(3, all.size())
        );

    }

    @Test
    void save_butExists() {
        // Arrange
        Album album = Album.builder().id(1L).build();

        // Act
        Album savedAlbum = repositorio.save(album);
        var all = repositorio.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album, savedAlbum),
                () -> assertEquals(2, all.size())
        );

    }

    @Test
    void deleteById_existingId() {
        // Act
        Long id = 1L;
        repositorio.deleteById(id);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteById_existingId",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repositorio.existsById(id))
        );
    }


    @Test
    void deleteByUuid_existingUuid() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        repositorio.deleteByUuid(uuid);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repositorio.existsByUuid(uuid))
        );
    }

    @Test
    void nextId() {
        // Act
        Long nextId = repositorio.nextId();
        var all = repositorio.findAll();

        // Assert
        assertAll("nextId",
                () -> assertEquals(3L, nextId),
                () -> assertEquals(2, all.size())
        );
    }
}