package albumes.mappers;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.models.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlbumMapper {
    public Album toAlbum(Long id, AlbumCreateDto createDto) {
        return Album.builder()
                .id(id)
                .nombre(createDto.getNombre())
                .artista(createDto.getArtista())
                .genero(createDto.getGenero())
                .precio(createDto.getPrecio())
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Album toAlbum(AlbumUpdateDto updateDto, Album album) {
        return Album.builder()
                .id(album.getId())
                .nombre(updateDto.getNombre() != null ? updateDto.getNombre() : album.getNombre())
                .genero(updateDto.getGenero() != null ? updateDto.getGenero() : album.getGenero())
                // Una vez creado el álbum, no se puede cambiar el artista
                .artista(album.getArtista())
                .precio(updateDto.getPrecio() != null ? updateDto.getPrecio() : album.getPrecio())
                .createdAt(album.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(album.getUuid())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .nombre(album.getNombre())
                .artista(album.getArtista())
                .genero(album.getGenero())
                .precio(album.getPrecio())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .uuid(album.getUuid())
                .build();
    }

    // Mapeamos de modelo a DTO (lista)
    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes) {
        return albumes.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }

}