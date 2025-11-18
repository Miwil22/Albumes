package artistas.services;

import artistas.dto.ArtistaRequestDto;
import artistas.exceptions.ArtistaConflictException;
import artistas.exceptions.ArtistaNotFoundException;
import artistas.mappers.ArtistaMapper;
import artistas.models.Artista;
import artistas.repositories.ArtistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceImplTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @Spy
    private ArtistaMapper artistaMapper;

    @InjectMocks
    private ArtistaServiceImpl artistaService;

    @Test
    void save_ShouldSaveArtista(){
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("Queen").build();
        Artista saved = Artista.builder().id(1L).nombre("Queen").build();

        when(artistaRepository.findByNombreEqualsIgnoreCase("Queen")).thenReturn(Optional.empty());
        when(artistaRepository.save(any(Artista.class))).thenReturn(saved);

        Artista result = artistaService.save(dto);

        assertEquals("Queen", result.getNombre());
    }

    @Test
    void save_ShouldThrowConflict_IfExists(){
        ArtistaRequestDto dto = ArtistaRequestDto.builder().nombre("Queen").build();
        Artista existing = Artista.builder().id(1L).nombre("Queen").build();

        when(artistaRepository.findByNombreEqualsIgnoreCase("Queen")).thenReturn(Optional.of(existing));

        assertThrows(ArtistaConflictException.class, () -> artistaService.save(dto));
    }

    @Test
    void findById_NotFount(){
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ArtistaNotFoundException.class, () -> artistaService.findById(99L));
    }

    @Test
    void delete_ShouldThrowConflict_IfHasAlbums(){
    when(artistaRepository.findById(1L)).thenReturn(Optional.of(Artista.builder().id(1L).build()));
    when(artistaRepository.existsAlbumById(1L)).thenReturn(true);

    assertThrows(ArtistaConflictException.class, () -> artistaService.deleteById(1L));
    }

}