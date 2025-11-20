package org.example.artistas.exceptions;

// Clase base. Todas las excepciones de Artistas heredarán de aquí.
public class ArtistaException extends RuntimeException {
    public ArtistaException(String message) {
        super(message);
    }
}