package org.example.artistas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Si lanzamos esto, Spring devolverá automáticamente un 404 NOT FOUND al usuario.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtistaNotFoundException extends RuntimeException { // Nota: Debería heredar de ArtistaException por limpieza, pero funciona igual.
    // Constructor por si buscamos por ID
    public ArtistaNotFoundException(Long id) {
        super("Artista con id " + id + " no encontrado");
    }
    // Constructor por si buscamos por nombre
    public ArtistaNotFoundException(String nombre){
        super("Artista " + nombre + " no encontrado");
    }
}