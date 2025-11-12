package albumes.controllers;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.exceptions.AlbumNotFoundException;
import albumes.services.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumRestControllerTest {

    private final String ENDPOINT = "/api/v1/albumes";

    private final AlbumResponseDto responseDto1 = AlbumResponseDto.builder()
            .id(1L).nombre("Abbey Road").artista("The Beatles").build();
    private final AlbumResponseDto responseDto2 = AlbumResponseDto.builder()
            .id(2L).nombre("Thriller").artista("Michael Jackson").build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void getAll() {
        var albumResponses = List.of(responseDto1, responseDto2);
        when(albumService.findAll(null, null)).thenReturn(albumResponses);

        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0].nombre").isEqualTo(responseDto1.getNombre());
                });

        verify(albumService, times(1)).findAll(null, null);
    }

    @Test
    void getAllByArtista() {
        var albumResponses = List.of(responseDto2);
        String queryString = "?artista=" + responseDto2.getArtista();
        when(albumService.findAll(isNull(), anyString())).thenReturn(albumResponses);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0].nombre").isEqualTo(responseDto2.getNombre());
                });

        verify(albumService, only()).findAll(isNull(), anyString());
    }


    @Test
    void getById_shouldReturnJsonWithAlbum_whenValidIdProvided() {
        Long id = 1L;
        when(albumService.findById(id)).thenReturn(responseDto1);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.nombre")
                .isEqualTo(responseDto1.getNombre());

        verify(albumService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowAlbumNotFound_whenInvalidIdProvided() {
        Long id = 99L;
        when(albumService.findById(anyLong())).thenThrow(new AlbumNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND) // 404
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundException.class);

        verify(albumService, only()).findById(anyLong());
    }

    @Test
    void create() {
        String requestBody = """
                   {
                      "nombre": "New Album",
                      "artista": "New Artist",
                      "genero": "Pop",
                      "precio": 10.99
                   }
                   """;

        var savedAlbum = AlbumResponseDto.builder()
                .id(3L).nombre("New Album").artista("New Artist").build();

        when(albumService.save(any(AlbumCreateDto.class))).thenReturn(savedAlbum);

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.nombre")
                .isEqualTo(savedAlbum.getNombre());

        verify(albumService, only()).save(any(AlbumCreateDto.class));
    }

    @Test
    void create_whenBadRequest_ValidationFails() {
        String requestBody = """
           {
              "nombre": "Test",
              "artista": "Test",
              "genero": "Cumbia",
              "precio": -5.0
           }
           """;

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST) // 400
                .bodyJson()
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("genero");
                    assertThat(path).hasFieldOrProperty("precio");
                });

        verify(albumService, never()).save(any(AlbumCreateDto.class));
    }

    @Test
    void update() {
        Long id = 1L;
        String requestBody = """
           {
              "nombre": "Updated Name",
              "precio": 500.0
           }
           """;

        var updatedAlbum = AlbumResponseDto.builder()
                .id(id).nombre("Updated Name").precio(500.0f).build();

        when(albumService.update(anyLong(), any(AlbumUpdateDto.class))).thenReturn(updatedAlbum);

        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.nombre")
                .isEqualTo(updatedAlbum.getNombre());

        verify(albumService, only()).update(anyLong(), any(AlbumUpdateDto.class));
    }

    @Test
    void updatePartial() {
        Long id = 1L;
        String requestBody = """
           {
              "precio": 500.0
           }
           """;

        var updatedAlbum = AlbumResponseDto.builder()
                .id(id).nombre("Updated Name").precio(500.0f).build();

        when(albumService.update(anyLong(), any(AlbumUpdateDto.class))).thenReturn(updatedAlbum);

        var result = mockMvcTester.patch()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatusOk();

        verify(albumService, only()).update(anyLong(), any(AlbumUpdateDto.class));
    }

    @Test
    void delete() {
        Long id = 1L;
        doNothing().when(albumService).deleteById(anyLong());
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(albumService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowAlbumNotFound_whenInvalidIdProvided() {
        Long id = 99L;
        doThrow(new AlbumNotFoundException(id)).when(albumService).deleteById(anyLong());

        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundException.class);

        verify(albumService, only()).deleteById(anyLong());
    }
}