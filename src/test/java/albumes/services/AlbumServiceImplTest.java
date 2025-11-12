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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private AlbumRepository albumRepository;

    @Spy
    private AlbumMapper albumMapper = new AlbumMapper();

    @InjectMocks
    private AlbumServiceImpl albumService;


    @BeforeEach
    void setUp() {
        responseDto1 = albumMapper.toAlbumResponseDto(album1);
    }

    @Test
    void findAll_ShouldReturnAllAlbums_WhenNoParametersProvided() {
        when(albumRepository.findAll()).thenReturn(List.of(album1, album2));

        List<AlbumResponseDto> res = albumService.findAll(null, null);

        assertAll(
                () -> assertEquals(2, res.size()),
                () -> assertEquals(responseDto1.getNombre(), res.get(0).getNombre())
        );
        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombre_WhenNombreParameterProvided() {
        when(albumRepository.findAllByNombre("Abbey Road")).thenReturn(List.of(album1));

        var res = albumService.findAll("Abbey Road", null);

        assertEquals(1, res.size());
        verify(albumRepository, only()).findAllByNombre("Abbey Road");
    }

    @Test
    void findAll_ShouldReturnAlbumsByArtista_WhenArtistaParameterProvided() {
        when(albumRepository.findAllByArtista("Michael")).thenReturn(List.of(album2));

        var res = albumService.findAll(null, "Michael");

        assertEquals(1, res.size());
        verify(albumRepository, only()).findAllByArtista("Michael");
    }

    @Test
    void findAll_ShouldReturnAlbumsByNombreAndArtista_WhenBothParametersProvided() {
        when(albumRepository.findAllByNombreAndArtista("Abbey", "Beatles")).thenReturn(List.of(album1));

        var res = albumService.findAll("Abbey", "Beatles");

        assertEquals(1, res.size());
        verify(albumRepository, only()).findAllByNombreAndArtista("Abbey", "Beatles");
    }

    @Test
    void findById_ShouldReturnAlbum_WhenValidIdProvided() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        var res = albumService.findById(1L);

        assertEquals(responseDto1, res);
        verify(albumRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        var res = assertThrows(AlbumNotFoundException.class, () -> albumService.findById(99L));
        assertEquals("Álbum con id 99 no encontrado.", res.getMessage());
        verify(albumRepository).findById(99L);
    }

    @Test
    void findByUuid_ShouldThrowAlbumBadUuid_WhenInvalidUuidProvided() {
        String badUuid = "1234";
        var res = assertThrows(AlbumBadUuidException.class, () -> albumService.findByUuid(badUuid));
        assertEquals("El UUID " + badUuid + " no es válido", res.getMessage());
        verify(albumRepository, never()).findByUuid(any(UUID.class));
    }

    @Test
    void save_ShouldReturnSavedAlbum() {
        AlbumCreateDto createDto = AlbumCreateDto.builder()
                .nombre("New Album")
                .artista("New Artist")
                .precio(10.0f)
                .build();

        Album albumAGuardar = albumMapper.toAlbum(3L, createDto);

        when(albumRepository.nextId()).thenReturn(3L);
        when(albumRepository.save(any(Album.class))).thenReturn(albumAGuardar);

        var res = albumService.save(createDto);

        assertEquals(3L, res.getId());
        assertEquals("New Album", res.getNombre());

        verify(albumRepository).nextId();
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void update_ShouldReturnUpdatedAlbum_WhenValidIdProvided() {
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder()
                .nombre("Updated Name")
                .precio(500.0f)
                .build();

        Album albumActualizado = albumMapper.toAlbum(updateDto, album1);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));
        when(albumRepository.save(any(Album.class))).thenReturn(albumActualizado);

        var res = albumService.update(1L, updateDto);

        assertEquals("Updated Name", res.getNombre());
        assertEquals(500.0f, res.getPrecio());

        verify(albumRepository).findById(1L);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void update_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        AlbumUpdateDto updateDto = AlbumUpdateDto.builder().build();
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(99L, updateDto))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id 99 no encontrado.");

        verify(albumRepository).findById(99L);
        verify(albumRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteAlbum_WhenValidIdProvided() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album1));

        albumService.deleteById(1L);

        verify(albumRepository).findById(1L);
        verify(albumRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowAlbumNotFound_WhenInvalidIdProvided() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.deleteById(99L))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Álbum con id 99 no encontrado.");

        verify(albumRepository, never()).deleteById(anyLong());
    }
}