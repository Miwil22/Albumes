package org.example.albumes.mappers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.example.artistas.models.Artista; // Importante: Necesitamos conocer el modelo de Artista
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

// @Component: Le dice a Spring: "Carga esta clase en memoria para que pueda usarla (inyectarla) en el Servicio".
@Component
public class AlbumMapper {

    // MÉTODO PARA CREAR (De DTO -> Entidad)
    // Recibe los datos crudos (dto) y el objeto Artista real de la base de datos.
    public Album toAlbum(AlbumCreateDto createDto, Artista artista) {
        return Album.builder()
                .nombre(createDto.getNombre())
                .genero(createDto.getGenero())
                .precio(createDto.getPrecio())
                .artista(artista) // Aquí "enchufamos" la relación con el padre.
                .build();
        // Nota: No ponemos ID ni UUID aquí, se generan automáticamente en la clase Album o en la BD.
    }

    // MÉTODO PARA ACTUALIZAR (De DTO + Entidad Vieja -> Entidad Nueva)
    // Coge los datos nuevos (updateDto) y los mezcla con los datos que ya existían (albumOriginal).
    public Album toAlbum(AlbumUpdateDto updateDto, Album albumOriginal) {
        return Album.builder()
                .id(albumOriginal.getId()) // Mantenemos el mismo ID
                .uuid(albumOriginal.getUuid()) // Mantenemos el mismo UUID
                .createdAt(albumOriginal.getCreatedAt()) // Mantenemos la fecha de creación original
                .artista(albumOriginal.getArtista()) // Mantenemos el mismo artista (no dejamos cambiarlo)

                // AQUÍ ESTÁ EL TRUCO DEL UPDATE:
                // "Si me envían un nombre nuevo, úsalo. Si es null, quédate con el que ya tenías".
                // Esto permite actualizaciones parciales (PATCH).
                .nombre(updateDto.getNombre() != null ? updateDto.getNombre() : albumOriginal.getNombre())
                .genero(updateDto.getGenero() != null ? updateDto.getGenero() : albumOriginal.getGenero())
                .precio(updateDto.getPrecio() != null ? updateDto.getPrecio() : albumOriginal.getPrecio())

                // Actualizamos la fecha de modificación a "ahora mismo".
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // MÉTODO DE SALIDA (De Entidad -> DTO Respuesta)
    // Prepara el objeto para enviarlo al cliente (JSON).
    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
                .id(album.getId())
                .nombre(album.getNombre())
                .genero(album.getGenero())
                .precio(album.getPrecio())
                // Sacamos solo el nombre del artista para que quede bonito en el JSON.
                .artista(album.getArtista().getNombre())
                .uuid(album.getUuid())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    // Método de utilidad para convertir una LISTA entera de álbumes a una lista de respuestas.
    // Usa el método anterior (toAlbumResponseDto) uno por uno.
    public java.util.List<AlbumResponseDto> toResponseDtoList(java.util.List<Album> albums) {
        return albums.stream().map(this::toAlbumResponseDto).toList();
    }
}