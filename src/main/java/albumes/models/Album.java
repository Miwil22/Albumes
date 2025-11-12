package albumes.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Album {
    private Long id;

    private String nombre;
    private String artista;
    private String genero;
    private Float precio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}