package org.example.albumes.dto;

import org.example.albumes.validators.GeneroValido;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

// @Builder: Permite crear el objeto fácilmente en los tests.
// @Data: Genera getters, setters, toString, etc.
@Builder
@Data
public class AlbumCreateDto {
    // @NotBlank: Valida que el campo no sea null y tenga al menos un carácter que no sea espacio.
    // Si falla, devuelve el mensaje especificado.
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @NotBlank(message = "El artista no puede estar vacío")
    private final String artista;

    // @GeneroValido: Esta es nuestra validación PERSONALIZADA.
    // Llama a la clase GeneroValidoValidator para comprobar si el género es correcto (Rock o Pop).
    @GeneroValido
    private final String genero;

    // @NotNull: El campo debe estar presente.
    @NotNull(message = "El precio no puede ser nulo")
    // @PositiveOrZero: El número debe ser >= 0.
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private final Float precio;
}