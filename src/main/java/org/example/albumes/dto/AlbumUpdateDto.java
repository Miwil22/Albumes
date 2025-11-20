package org.example.albumes.dto;

import org.example.albumes.validators.GeneroValido;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlbumUpdateDto {
    // No ponemos @NotBlank aquí porque en una actualización (PATCH) el nombre podría ser opcional
    // (si solo quieres cambiar el precio, no envías el nombre).
    private final String nombre;

    // Fíjate: NO hay campo 'artista'.
    // Decisión de diseño: Una vez creado el álbum, no permitimos cambiar su artista
    // (sería raro que un disco de Queen pase a ser de Shakira).
    // private final String artista;

    // Validamos que si envían un género, sea uno de los permitidos (Rock/Pop).
    @GeneroValido
    private final String genero;

    // Validamos que si cambian el precio, no pongan uno negativo.
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private final Float precio;
}