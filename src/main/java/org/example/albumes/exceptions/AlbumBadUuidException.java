package org.example.albumes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus(HttpStatus.BAD_REQUEST): Devuelve un código 400.
// Se usa cuando alguien intenta buscar por un UUID que no tiene el formato correcto (ej: "1234").
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadUuidException extends AlbumException {
    public AlbumBadUuidException(String uuid) {
        super("El UUID " + uuid + " no es válido");
    }
}