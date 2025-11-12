package albumes.mappers;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumUpdateDto;
import albumes.models.Album;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {

    // Inyectamos el mapper
    private final AlbumMapper albumMapper = new AlbumMapper();

    @Test
    void toAlbum_create() {
        // Arrange
        Long id = 1L;
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles")
                .genero("Rock")
                .precio(19.99f)
                .build();
        // Act
        var res = albumMapper.toAlbum(id, createDto);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(createDto.getNombre(), res.getNombre()),
                () -> assertEquals(createDto.getArtista(), res.getArtista()),
                () -> assertEquals(createDto.getGenero(), res.getGenero()),
                () -> assertEquals(createDto.getPrecio(), res.getPrecio())
        );
    }

    @Test
    void toAlbum_update() {
        // Arrange
        Long id = 1L;
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .nombre("Abbey Road (Updated)")
                .genero("Rock")
                .precio(29.99f)
                .build();

        Album album = Album.builder()
                .id(id)
                .nombre(updateDto.getNombre())
                .genero(updateDto.getGenero())
                .precio(updateDto.getPrecio())
                .build();
        // Act
        var res = albumMapper.toAlbum(updateDto, album);
        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(updateDto.getNombre(), res.getNombre()),
                () -> assertEquals(updateDto.getGenero(), res.getGenero()),
                () -> assertEquals(updateDto.getPrecio(), res.getPrecio())
        );
    }

    @Test
    void toAlbumResponseDto() {
        // Arrange
        Album album = Album.builder()
                .id(1L)
                .nombre("Abbey Road")
                .artista("The Beatles")
                .genero("Rock")
                .precio(19.99f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
                .build();
        // Act
        var res = albumMapper.toAlbumResponseDto(album);
        // Assert
        assertAll(
                () -> assertEquals(album.getId(), res.getId()),
                () -> assertEquals(album.getNombre(), res.getNombre()),
                () -> assertEquals(album.getArtista(), res.getArtista()),
                () -> assertEquals(album.getGenero(), res.getGenero()),
                () -> assertEquals(album.getPrecio(), res.getPrecio())
        );
    }
}