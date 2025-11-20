package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Devuelve un 409 CONFLICT. Se usa cuando intentas crear algo que choca con lo que ya hay (duplicados)
// o borrar algo que está bloqueado.
@ResponseStatus(HttpStatus.CONFLICT)
public class ArtistaConflictException extends RuntimeException {
    public ArtistaConflictException(String message) {
        super(message);
    }
}