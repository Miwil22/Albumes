package albumes.services;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.exceptions.AlbumBadUuidException;
import albumes.exceptions.AlbumNotFoundException;
import albumes.mappers.AlbumMapper;
import albumes.models.Album;
import albumes.repositories.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integra Mockito con JUnit5 para poder usar mocks, espías y capturadores en los tests
@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    private final Album album1 = Album.builder()
            .id(1L).nombre("Abbey Road").artista("The Beatles")
            .genero("Rock").precio(19.99f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L).nombre("Thriller").artista("Michael Jackson")
            .genero("Pop").precio(15.29f)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto responseDto1;

    // usamos el repositorio totalmente simulado
    @Mock
    private AlbumRepository albumRepository;

    // usamos el mapper real aunque en modo espía que nos permite simular algunas partes del mismo
    @Spy
    private AlbumMapper albumMapper;

    // Es la clase que se testea y a la que se inyectan los mocks y espías automáticamente
    @InjectMocks
    private AlbumServiceImpl albumService;

    // Capturador de argumentos
    @Captor
    private ArgumentCaptor<Album> albumCaptor;


    @BeforeEach
    void setUp() {
        responseDto1 = albumMapper.toAlbumResponseDto(album1);
        // Quizá no la necesitemos
        // responseDto2 = albumMapper.toAlbumResponseDto(album2);
    }

    @Test
    void findAll_ShouldReturnAllAlbums_WhenNoParametersProvided() {
        // Arrange
        List<Album> expectedAlbums = Arrays.asList(album1, album2);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findAll()).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(null, null);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        // verifica que findAll() se ejecuta una vez
        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombre_WhenNombreParameterProvided() {
        // Arrange
        String nombre = "Abbey Road";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findAllByNombre(nombre)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(nombre, null);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        // Verifica que solo se ejecuta este método
        verify(albumRepository, only()).findAllByNombre(nombre);
    }

    @Test
    void findAll_ShouldReturnAlbumsByArtista_WhenArtistaParameterProvided() {
        // Arrange
        String artista = "The Beatles";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findAllByArtista(artista)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(null, artista);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        verify(albumRepository, only()).findAllByArtista(artista);
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombreAndArtista_WhenBothParametersProvided() {
        // Arrange
        String nombre = "Abbey Road";
        String artista = "The Beatles";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponses = albumMapper.toResponseDtoList(expectedAlbums);
        when(albumRepository.findAllByNombreAndArtista(nombre, artista)).thenReturn(expectedAlbums);

        // Act
        List<AlbumResponseDto> actualAlbumResponses = albumService.findAll(nombre, artista);

        // Assert
        assertIterableEquals(expectedAlbumResponses, actualAlbumResponses);

        // Verify
        verify(albumRepository, only()).findAllByNombreAndArtista(nombre, artista);
    }

    @Test
    void findById_ShouldReturnAlbum_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        AlbumResponseDto expectedAlbumResponse = responseDto1;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.findById(id);

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository, only()).findById(id);
    }

    @Test
    void findById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(AlbumNotFoundException.class, () -> albumService.findById(id));
        assertEquals("Álbum con id " + id + " no encontrado.", res.getMessage());

        // Verify
        // verifica que se ejecuta el método
        verify(albumRepository).findById(id);
    }


    @Test
    void findByUuid_ShouldReturnAlbum_WhenValidUuidProvided() {
        // Arrange
        UUID expectedUuid = album1.getUuid();
        AlbumResponseDto expectedAlbumResponse = responseDto1;
        when(albumRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(album1));

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.findByUuid(expectedUuid.toString());

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository, only()).findByUuid(expectedUuid);
    }

    @Test
    void findByUuid_ShouldThrowAlbumBadUuid_WhenInvalidUuidProvided() {
        // Arrange
        //String uuid = "3a31d097-23cf-4b8d-989a-96e380cc996b";
        String uuid = "1234";
        // Act & Assert
        var res = assertThrows(AlbumBadUuidException.class, () -> albumService.findByUuid(uuid));
        assertEquals("El UUID " + uuid + " no es válido", res.getMessage());

        // Verify
        // verifica que no se ha ejecutado
        verify(albumRepository, never()).findByUuid(any());
    }

    @Test
    void save_ShouldReturnSavedAlbum_WhenValidAlbumCreateDtoProvided() {
        // Arrange
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("New Album")
                .artista("New Artist")
                .precio(10.0f)
                .build();
        Album expectedAlbum = Album.builder()
                .id(1L)
                .nombre("New Album")
                .artista("New Artist")
                .precio(10.0f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
        AlbumResponseDto expectedAlbumResponse = albumMapper.toAlbumResponseDto(expectedAlbum);

        when(albumRepository.nextId()).thenReturn(1L);
        when(albumRepository.save(any(Album.class))).thenReturn(expectedAlbum);

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.save(createDto);

        // Assert
        assertEquals(expectedAlbumResponse, actualAlbumResponse);

        // Verify
        verify(albumRepository).nextId();
        verify(albumRepository).save(albumCaptor.capture());

        Album albumCaptured = albumCaptor.getValue();
        assertEquals(expectedAlbum.getNombre(), albumCaptured.getNombre());
        // equivalente con AsssertJ en lugar de JUnit
        //assertThat(albumCaptured.getNombre()).isEqualTo(expectedAlbum.getNombre());


    }

    @Test
    void update_ShouldReturnUpdatedAlbum_WhenValidIdAndAlbumUpdateDtoProvided() {
        // Arrange
        Long id = 1L;
        Float precio = 500.0f;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .precio(precio)
                .build();
        Album albumUpdate = albumMapper.toAlbum(updateDto, album1);
        when(albumRepository.save(any(Album.class))).thenReturn(albumUpdate);

        responseDto1.setPrecio(precio);
        AlbumResponseDto expectedAlbumResponse = responseDto1;

        // Act
        AlbumResponseDto actualAlbumResponse = albumService.update(id, updateDto);

        // Assert
        // con Junit da error
        //assertEquals(expectedAlbumResponse, actualAlbumResponse);
        // con AssertJ podemos excluir algún campo
        assertThat(actualAlbumResponse)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(expectedAlbumResponse);

        // Verify
        verify(albumRepository).findById(id);
        verify(albumRepository).save(any());
    }

    @Test
    void update_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .precio(500.0f)
                .build();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        // con AssertJ
        assertThatThrownBy(
                () -> albumService.update(id, updateDto))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id " + id + " no encontrado.");
        // con JUnit
        //var res = assertThrows(AlbumNotFoundException.class,
        //    () -> albumService.update(id, updateDto));
        //assertEquals("Álbum con id " + id + " no encontrada.", res.getMessage());

        // Verify
        verify(albumRepository).findById(id);
        verify(albumRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteAlbum_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        // Act
        // con AssertJ
        assertThatCode(() -> albumService.deleteById(id))
                .doesNotThrowAnyException();

        // Assert
        verify(albumRepository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        // con JUnit
        //var res = assertThrows(AlbumNotFoundException.class, () -> albumService.deleteById(id));
        //assertEquals("Álbum con id " + id + " no encontrada.", res.getMessage());
        // El equivalente con AssertJ
        assertThatThrownBy(() -> albumService.deleteById(id))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id " + id + " no encontrado.");

        // Verify
        verify(albumRepository, never()).deleteById(id);
    }
}