package albumes.controllers;

import org.example.Application;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.services.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class) // Arranca el contexto completo de Spring
@AutoConfigureMockMvc // Configura el simulador de peticiones HTTP (MockMvc)
class AlbumRestControllerTest {

    private final String ENDPOINT = "/api/v1/albumes";

    // DTOs de respuesta simulados
    private final AlbumResponseDto albumResponse1 = AlbumResponseDto.builder()
            .id(1L).nombre("Abbey Road").artista("The Beatles").build();

    @Autowired
    private MockMvcTester mockMvcTester; // Herramienta moderna para lanzar peticiones

    @MockitoBean // Sustituye el Servicio real por un Mock en el contexto de Spring
    private AlbumService albumService;

    @Test
    void getAll() {
        // Arrange: Si el controlador llama al servicio, este devuelve la lista preparada.
        when(albumService.findAll(null, null)).thenReturn(List.of(albumResponse1));

        // Act: Hacemos un GET a la URL
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: Verificamos status 200 y el contenido del JSON
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$[0].nombre").isEqualTo("Abbey Road");
                });
    }

    @Test
    void create() {
        // JSON que enviamos en el cuerpo
        String requestBody = """
                   {
                      "nombre": "New Album",
                      "artista": "New Artist",
                      "genero": "Pop",
                      "precio": 10.99
                   }
                   """;

        var savedAlbum = AlbumResponseDto.builder().id(3L).nombre("New Album").build();
        when(albumService.save(any(AlbumCreateDto.class))).thenReturn(savedAlbum);

        // Act: Hacemos un POST
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: Esperamos un 201 Created
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.nombre")
                .isEqualTo("New Album");
    }

    @Test
    void create_whenBadRequest_ValidationFails() {
        // JSON inválido (precio negativo)
        String requestBody = """
           {
              "nombre": "Test",
              "artista": "Test",
              "genero": "Rock",
              "precio": -5.0 
           }
           """;

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: Esperamos un 400 Bad Request y que el JSON de error mencione el campo precio
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("precio");
                });
    }
}