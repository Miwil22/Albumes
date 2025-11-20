package org.example.albumes.exceptions;

// Esta la usamos para errores generales de petición incorrecta (aunque aquí no tiene @ResponseStatus,
// normalmente se gestionaría para devolver un 400).
public class AlbumBadRequestException extends AlbumException {
    public AlbumBadRequestException(String message) {
        super(message);
    }
}