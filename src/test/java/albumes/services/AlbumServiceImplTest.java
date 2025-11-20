package albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import org.example.albumes.services.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): Habilita el uso de @Mock y @InjectMocks.
@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    // Datos de prueba estáticos
    private final Artista artista = Artista.builder().id(1L).nombre("The Beatles").build();
    private final Album album1 = Album.builder().id(1L).nombre("Abbey Road").artista(artista).precio(19.99f).build();
    private final Album album2 = Album.builder().id(2L).nombre("Thriller").precio(15.29f).build();

    // @Mock: Crea un objeto FALSO. No es el repositorio real.
    // Nosotros controlaremos qué devuelve cuando el servicio lo llame.
    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistaService artistaService;

    // @Spy: Usa el objeto REAL pero nos permite vigilarlo.
    // Queremos que el mapper funcione de verdad para probar las conversiones.
    @Spy
    private AlbumMapper albumMapper = new AlbumMapper();

    // @InjectMocks: Crea una instancia del Servicio real e inyecta dentro los Mocks creados arriba.
    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    void findAll_ShouldReturnAllAlbums() {
        // Arrange: "Cuando alguien llame a findAll en el repositorio falso, devuelve esta lista fija".
        when(albumRepository.findAll()).thenReturn(List.of(album1, album2));

        // Act: Llamamos al servicio.
        List<AlbumResponseDto> res = albumService.findAll(null, null);

        // Assert: Comprobamos el resultado.
        assertEquals(2, res.size());
    }

    @Test
    void findById_ShouldReturnAlbum() {
        // Simulamos que el repositorio encuentra el ID 1.
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        var res = albumService.findById(1L);

        // Verificamos que nos devuelve el DTO correcto.
        assertEquals(album1.getNombre(), res.getNombre());
    }

    @Test
    void save_ShouldReturnSavedAlbum() {
        // Arrange: Datos de entrada
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("Abbey Road")
                .artista("The Beatles")
                .precio(19.99f)
                .build();

        // 1. Simulamos que el servicio de artistas encuentra al padre.
        when(artistaService.findByNombre("The Beatles")).thenReturn(artista);

        // 2. Simulamos que al guardar, el repositorio devuelve el objeto ya guardado.
        when(albumRepository.save(any(Album.class))).thenReturn(album1);

        // Act
        var res = albumService.save(createDto);

        // Assert
        assertEquals("Abbey Road", res.getNombre());

        // Verify: Nos aseguramos de que el servicio llamó a los métodos necesarios.
        verify(artistaService).findByNombre("The Beatles");
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void deleteById_ShouldDelete() {
        // Simulamos que existe para que no falle la comprobación inicial.
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        albumService.deleteById(1L);

        // Verificamos que se llamó al método delete del repositorio.
        verify(albumRepository).deleteById(1L);
    }

    @Test
    void findById_NotFound() {
        // Simulamos que el repositorio devuelve vacío.
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert: Esperamos que el servicio lance la excepción.
        assertThrows(AlbumNotFoundException.class, () -> albumService.findById(99L));
    }
}