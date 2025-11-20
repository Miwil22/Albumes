package org.example.albumes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumResponseDto {
    // Aquí SÍ incluimos el ID y el UUID, porque el cliente necesita saberlos para identificar el dato.
    private Long id;

    private String nombre;

    // ¡OJO! Aquí 'artista' es un String (el nombre), no el objeto Artista entero.
    // Esto "aplana" la respuesta para que sea más fácil de leer para el cliente.
    private String artista;

    private String genero;
    private Float precio;

    // Devolvemos las fechas de auditoría para que se sepa cuándo se creó.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}