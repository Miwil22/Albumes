package albumes.mappers;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumUpdateDto;
import albumes.models.Album;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {

    private final AlbumMapper albumMapper = new AlbumMapper();

    @Test
    void toAlbum_create() {
        Long id = 1L;
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Test Album")
                .artista("Test Artist")
                .genero("Rock")
                .precio(9.99f)
                .build();
        var res = albumMapper.toAlbum(id, createDto);

        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(createDto.getNombre(), res.getNombre()),
                () -> assertEquals(createDto.getArtista(), res.getArtista()),
                () -> assertEquals(createDto.getGenero(), res.getGenero()),
                () -> assertEquals(createDto.getPrecio(), res.getPrecio()),
                () -> assertNotNull(res.getUuid()),
                () -> assertNotNull(res.getCreatedAt())
        );
    }

    @Test
    void toAlbum_update() {
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .nombre("Updated Name")
                .precio(12.99f)
                .build();

        Album original = Album.builder()
                .id(1L)
                .nombre("Original Name")
                .artista("Original Artist")
                .genero("Pop")
                .precio(9.99f)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        var res = albumMapper.toAlbum(updateDto, original);

        // Assert
        assertAll(
                () -> assertEquals(original.getId(), res.getId()),
                () -> assertEquals(updateDto.getNombre(), res.getNombre()),
                () -> assertEquals(original.getArtista(), res.getArtista()), // No cambia
                () -> assertEquals(original.getGenero(), res.getGenero()), // No cambia
                () -> assertEquals(updateDto.getPrecio(), res.getPrecio()),
                () -> assertEquals(original.getUuid(), res.getUuid()),
                () -> assertEquals(original.getCreatedAt(), res.getCreatedAt()),
                () -> assertTrue(res.getUpdatedAt().isAfter(original.getUpdatedAt()))
        );
    }

    @Test
    void toAlbumResponseDto() {
        Album album = Album.builder()
                .id(1L)
                .nombre("Test Album")
                .artista("Test Artist")
                .genero("Rock")
                .precio(9.99f)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var res = albumMapper.toAlbumResponseDto(album);

        assertAll(
                () -> assertEquals(album.getId(), res.getId()),
                () -> assertEquals(album.getNombre(), res.getNombre()),
                () -> assertEquals(album.getArtista(), res.getArtista()),
                () -> assertEquals(album.getUuid(), res.getUuid())
        );
    }
}