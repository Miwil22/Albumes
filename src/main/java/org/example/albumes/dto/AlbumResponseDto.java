package org.example.albumes.dto;

import org.example.albumes.exceptions.AlbumException;

public class AlbumBadRequestException extends AlbumException {
    public AlbumBadRequestException(String message) {
        super(message);
    }
}