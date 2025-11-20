package org.example.artistas.services;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;

import java.util.List;

// Esta es la INTERFAZ. Es el "contrato" que deben cumplir quienes quieran ser un servicio de artistas.
// Aquí solo decimos QUÉ se puede hacer, no CÓMO se hace.
public interface ArtistaService {

    // Método para obtener una lista de artistas.
    // Puede recibir un nombre para filtrar (ej: "Queen") o null para traerlos todos.
    List<Artista> findAll(String nombre);

    // Método para buscar un artista específico por su nombre exacto.
    // Devuelve el objeto Artista completo.
    Artista findByNombre(String nombre);

    // Método para buscar un artista por su ID numérico (clave primaria).
    // Si no lo encuentra, la implementación (el Chef) debería lanzar un error.
    Artista findById(Long id);

    // Método para GUARDAR (Crear) un artista nuevo.
    // Recibe un DTO (la caja con los datos que envió el usuario) y devuelve el Artista ya guardado en BD.
    Artista save(ArtistaRequestDto artistaRequestDto);

    // Método para ACTUALIZAR un artista existente.
    // Necesita el ID para saber cuál cambiar y el DTO con los datos nuevos.
    Artista update(Long id, ArtistaRequestDto artistaRequestDto);

    // Método para BORRAR un artista por su ID.
    // No devuelve nada (void).
    void deleteById(Long id);
}