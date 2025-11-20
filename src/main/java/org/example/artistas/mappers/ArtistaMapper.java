package org.example.artistas.mappers;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.springframework.stereotype.Component;

// @Component: Fundamental para que Spring lo detecte y podamos usarlo en el Servicio.
@Component
public class ArtistaMapper {

    // Para CREAR: Convierte el DTO en un objeto Artista nuevo.
    // El ID es null porque la base de datos se lo pondrá.
    public Artista toArtista(ArtistaRequestDto dto){
        return Artista.builder()
                .id(null)
                .nombre(dto.getNombre())
                // Si nos pasan isDeleted úsalo, si no, por defecto es false (activo).
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false)
                .build();
    }

    // Para ACTUALIZAR: Mezcla los datos nuevos (dto) con el artista viejo (artista).
    public Artista toArtista(ArtistaRequestDto dto, Artista artista){
        return Artista.builder()
                .id(artista.getId()) // Mantiene el ID original
                // Si el nombre viene en el DTO, lo cambiamos. Si es null, dejamos el viejo.
                .nombre(dto.getNombre() != null ? dto.getNombre() : artista.getNombre())
                .createdAt(artista.getCreatedAt()) // Mantiene fecha creación
                // Actualizamos estado de borrado si viene en el DTO
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : artista.getIsDeleted())
                .build();
    }
}