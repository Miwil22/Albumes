package albumes.services;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.exceptions.AlbumBadUuidException;
import albumes.exceptions.AlbumNotFoundException;
import albumes.mappers.AlbumMapper;
import albumes.models.Album;
import albumes.repositories.AlbumRepository;
import artistas.models.Artista; // Importante: Necesitamos el modelo de Artista
import artistas.services.ArtistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): Esta línea es OBLIGATORIA para tests unitarios.
// Le dice a JUnit: "No arranques Spring entero (base de datos, servidor web...).
// Solo carga la librería Mockito para simular objetos falsos". Es mucho más rápido.
@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    // --- 1. PREPARACIÓN DE DATOS FALSOS (DUMMY DATA) ---
    // Creamos objetos Java normales para usarlos en las pruebas.
    // No vienen de la base de datos, los inventamos aquí.

    private final Artista artista1 = Artista.builder()
            .id(1L)
            .nombre("The Beatles")
            .build();

    // ARREGLO: Creamos un segundo artista para el segundo álbum
    private final Artista artista2 = Artista.builder()
            .id(2L)
            .nombre("Michael Jackson")
            .build();

    private final Album album1 = Album.builder()
            .id(1L).nombre("Abbey Road")
            .artista(artista1) // Asignamos el padre
            .genero("Rock").precio(19.99f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    // ARREGLO DEL ERROR: Antes aquí no poníamos artista, por eso fallaba el Mapper (NullPointerException).
    private final Album album2 = Album.builder()
            .id(2L).nombre("Thriller")
            .artista(artista2) // <--- ¡IMPORTANTE! Asignamos el artista.
            .genero("Pop").precio(15.29f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto responseDto1;

    // --- 2. CREACIÓN DE SIMULACROS (MOCKS) ---

    // @Mock: Crea un objeto "tonto" o falso.
    // El 'albumRepository' real conectaría con la BD. Este NO hace nada.
    // Nosotros tendremos que "entrenarlo" (con when...) para decirle qué devolver.
    @Mock
    private AlbumRepository albumRepository;

    // @Mock: También simulamos el servicio de artistas, porque nuestro servicio lo llama al guardar.
    @Mock
    private ArtistaService artistaService;

    // @Spy: Usamos el Mapper REAL, no uno falso.
    // "Spy" significa que usamos el objeto de verdad pero Mockito nos deja vigilarlo.
    // Lo usamos porque queremos probar que la conversión de datos funciona bien.
    @Spy
    private AlbumMapper albumMapper = new AlbumMapper();

    // @InjectMocks: Este es el protagonista del test.
    // Crea una instancia REAL de 'AlbumServiceImpl' e INYECTA dentro los @Mock y @Spy que definimos arriba.
    @InjectMocks
    private AlbumServiceImpl albumService;

    // @Captor: Es una herramienta para "cazar" el argumento que se le pasa a un método.
    // Útil para comprobar qué objeto exacto se intentó guardar en la base de datos.
    @Captor
    private ArgumentCaptor<Album> albumCaptor;


    // @BeforeEach: Se ejecuta antes de cada @Test. Sirve para reiniciar variables.
    @BeforeEach
    void setUp() {
        // Preparamos un DTO de respuesta esperado para comparar en los tests.
        responseDto1 = albumMapper.toAlbumResponseDto(album1);
    }

    // --- 3. LOS TESTS ---

    @Test
    void findAll_ShouldReturnAllAlbums() {
        // ARRANGE (Preparar el escenario):
        // Le decimos al repositorio falso: "Cuando alguien llame a findAll(), devuelve esta lista de 2 álbumes".
        when(albumRepository.findAll()).thenReturn(List.of(album1, album2));

        // ACT (Acción):
        // Llamamos al método del servicio que queremos probar.
        List<AlbumResponseDto> res = albumService.findAll(null, null);

        // ASSERT (Verificar el resultado):
        // Comprobamos que nos ha devuelto 2 cosas y que los datos son correctos.
        assertEquals(2, res.size());
        assertEquals("Abbey Road", res.get(0).getNombre());
        // Ahora esto no fallará porque album2 ya tiene artista:
        assertEquals("Thriller", res.get(1).getNombre());

        // VERIFY (Verificar comportamiento):
        // Aseguramos que el servicio llamó al método findAll del repositorio exactamente 1 vez.
        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnAlbum_WhenValidIdProvided() {
        // Arrange: Simulamos que el repositorio encuentra el álbum con ID 1.
        // Usamos Optional.of(...) porque el repositorio devuelve un Optional.
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        // Act
        var res = albumService.findById(1L);

        // Assert
        assertEquals(responseDto1, res); // Compara el objeto devuelto con el esperado.

        verify(albumRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange: Simulamos que el repositorio NO encuentra nada (Optional.empty).
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert:
        // Usamos assertThrows para decir: "Espero que al ejecutar esto, el programa lance una excepción".
        // Si NO lanza la excepción, el test falla (rojo).
        var res = assertThrows(AlbumNotFoundException.class, () -> albumService.findById(99L));

        // Comprobamos que el mensaje de error es el correcto.
        assertEquals("Álbum con id 99 no encontrado.", res.getMessage());

        verify(albumRepository).findById(99L);
    }

    @Test
    void save_ShouldReturnSavedAlbum() {
        // Arrange: Creamos el DTO que enviaría el usuario (sin ID).
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("New Album")
                .artista("New Artist") // Nombre del artista (String)
                .precio(10.0f)
                .build();

        // Creamos un objeto Artista falso que simula ser el que encontramos en la BD.
        Artista artistaNuevo = Artista.builder().id(3L).nombre("New Artist").build();

        // Creamos el Álbum que esperamos que se guarde (con ID y todo).
        Album albumGuardado = Album.builder()
                .id(1L).nombre("New Album").artista(artistaNuevo).precio(10.0f).build();

        // MOCKS:
        // 1. Cuando el servicio busque el artista, devolvemos el artista simulado.
        when(artistaService.findByNombre("New Artist")).thenReturn(artistaNuevo);

        // 2. Cuando el servicio pida el siguiente ID.
        when(albumRepository.nextId()).thenReturn(1L);

        // 3. Cuando el servicio mande guardar, devolvemos el álbum ya "guardado".
        // 'any(Album.class)' significa: "acepta cualquier objeto Album como parámetro".
        when(albumRepository.save(any(Album.class))).thenReturn(albumGuardado);

        // Act
        AlbumResponseDto result = albumService.save(createDto);

        // Assert
        assertEquals("New Album", result.getNombre());

        // Verify: Comprobamos que se buscó al artista antes de guardar.
        verify(artistaService).findByNombre("New Artist");
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void deleteById_ShouldDelete() {
        // Arrange: Simulamos que el álbum existe (para evitar el error 404 antes de borrar).
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        // Act
        albumService.deleteById(1L);

        // Assert: Verificamos que se llamó al método delete del repositorio.
        verify(albumRepository).deleteById(1L);
    }
}