package albumes.dto;

import albumes.validators.GeneroValido;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlbumUpdateDto {
    private final String nombre;
    private final String artista;

    @GeneroValido
    private final String genero;

    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private final Float precio;
}
