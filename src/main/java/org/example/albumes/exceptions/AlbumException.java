package org.example.albumes.exceptions;

// Heredamos de RuntimeException para que sean excepciones "no chequeadas" (no obligan a poner try-catch).
public abstract class AlbumException extends RuntimeException {
    public AlbumException(String message) {
        super(message);
    }
}