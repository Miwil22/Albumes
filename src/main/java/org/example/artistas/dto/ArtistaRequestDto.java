package org.example.artistas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArtistaRequestDto {
    // @NotBlank: Validación. El usuario no puede enviarnos un artista sin nombre o con el nombre vacío.
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    // Este campo es opcional. Sirve para marcarlo como borrado directamente al actualizar (Soft Delete).
    private final Boolean isDeleted;
}