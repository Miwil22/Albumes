package artistas.services;

import artistas.dto.ArtistaRequestDto;
import artistas.models.Artista;

import java.util.List;

public interface ArtistaService {
    List<Artista> findAll(String nombre);

    Artista findByNombre(String nombre);

    Artista findById(Long id);

    Artista save(ArtistaRequestDto artistaRequestDto);

    Artista update(Long id, ArtistaRequestDto artistaRequestDto);

    void deleteById(Long id);
}