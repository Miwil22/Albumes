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
    public Album toAlbum(Long id, AlbumCreateDto createDto){
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

    public Album toAlbum(AlbumUpdateDto updateDto, Album albumOriginal){
        return Album.builder()
                .id(albumOriginal.getId())
                .nombre(updateDto.getNombre() != null ? updateDto.getNombre() : albumOriginal.getNombre())
                .artista(updateDto.getArtista() != null ? updateDto.getArtista() : albumOriginal.getArtista())
                .genero(updateDto.getGenero() != null ? updateDto.getGenero() : albumOriginal.getGenero())
                .precio(updateDto.getPrecio() != null ? updateDto. getPrecio() : albumOriginal.getPrecio())
                .uuid(albumOriginal.getUuid())
                .createdAt(albumOriginal.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public AlbumResponseDto toAlbumResponseDto(Album album){
        return AlbumResponseDto.builder()
                .id(album.getId())
                .nombre(album.getNombre())
                .artista(album.getArtista())
                .genero(album.getGenero())
                .precio(album.getPrecio())
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes){
        return albumes.stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }
}
