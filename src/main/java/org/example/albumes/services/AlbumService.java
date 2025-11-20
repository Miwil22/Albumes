package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;

import java.util.List;

// Interfaz: Define QUÉ puede hacer nuestra aplicación con los álbumes, pero no CÓMO.
// Esto permite cambiar la lógica interna sin romper el resto de la aplicación (Desacoplamiento).
public interface AlbumService {

    // Buscar con filtros opcionales
    List<AlbumResponseDto> findAll(String nombre, String artista);

    // Buscar uno concreto
    AlbumResponseDto findById(Long id);

    // Buscar por el código seguro
    AlbumResponseDto findByUuid(String uuid);

    // Guardar uno nuevo
    AlbumResponseDto save(AlbumCreateDto createDto);

    // Actualizar uno existente
    AlbumResponseDto update(Long id, AlbumUpdateDto updateDto);

    // Borrar
    void deleteById(Long id);
}