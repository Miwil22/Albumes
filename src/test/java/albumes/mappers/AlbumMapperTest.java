package albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.mappers.AlbumMapper;
import org.example.artistas.models.Artista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMapperTest {

    private final AlbumMapper albumMapper = new AlbumMapper();
    private final Artista artista = Artista.builder().id(1L).nombre("The Beatles").build();

    @Test
    void toAlbum_create() {
        // Arrange: Preparamos un DTO y un Artista
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles") // Nombre en DTO
                .genero("Rock")
                .precio(19.99f)
                .build();

        // Act: Convertimos usando el objeto artista real
        var res = albumMapper.toAlbum(createDto, artista);

        // Assert: Comprobamos que el objeto Album resultante tiene el objeto Artista dentro
        assertAll(
                () -> assertEquals(createDto.getNombre(), res.getNombre()),
                () -> assertEquals(artista, res.getArtista()) // Aquí comprobamos la relación
        );
    }
}