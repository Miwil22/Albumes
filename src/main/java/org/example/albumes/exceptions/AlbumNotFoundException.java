package org.example.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

// @ResponseStatus(HttpStatus.NOT_FOUND): ¡MAGIA!
// Cuando lancemos esta excepción, Spring automáticamente devolverá al cliente un código HTTP 404 (Not Found).
// No hace falta configurar nada más en el controlador para que esto funcione.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlbumNotFoundException extends AlbumException {
    // Constructor para buscar por ID numérico
    public AlbumNotFoundException(Long id) {
        super("Álbum con id " + id + " no encontrado.");
    }
    // Constructor por si buscamos por UUID
    public AlbumNotFoundException(UUID uuid) {
        super("Álbum con uuid " + uuid + " no encontrado.");
    }
}