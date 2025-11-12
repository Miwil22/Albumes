package albumes.repositories;

import albumes.models.Album;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class AlbumRepositoryImpl  implements AlbumRepository{

    private final Map<Long, Album> albumes = new LinkedHashMap<>();

    //Algunos datos de ejemplo
    public AlbumRepositoryImpl(){
        save(Album.builder()
                .id(1L).nombre("Abbey Road").artista("The Beatles")
                .genero("Rock").precio(19.99f)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID()).build());
        save(Album.builder()
                .id(2L).nombre("Thriller").artista("Michael Jackson")
                .genero("Pop").precio(29.99f)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID()).build());
    }

    @Override
    public List<Album> findAll(){
        log.info("Buscando todos los álbumes");
        return albumes.values().stream().toList();
    }

    @Override
    public List<Album> findAllByNombre(String nombre){
        log.info("Buscando álbumes por nombre: {}", nombre);
        return albumes.values().stream()
                .filter(a -> a.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<Album> findAllByArtista(String artista){
        log.info("Buscando álbumes por artista: {}", artista);
        return albumes.values().stream()
                .filter(a -> a.getArtista().toLowerCase().contains(artista.toLowerCase()))
                .toList();
    }
    @Override
    public List<Album> findAllByNombreAndArtista(String nombre, String artista){
        log.info("Buscando álbumes por nombre: {} y artista: {}", nombre, artista);
        return albumes.values().stream()
                .filter(a -> a.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(a -> a.getArtista().toLowerCase().contains(artista.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Album> findById(Long id){
        log.info("Buscando álbum por id: {}", id);
        return Optional.ofNullable(albumes.get(id));
    }

    @Override
    public Optional<Album>  findByUuid(UUID uuid){
        log.info("Buscando álbum por uuid: {}", uuid);
        return albumes.values().stream()
                .filter(a -> a.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id){
        return albumes.containsKey(id);
    }

    @Override
    public boolean existsByUuid(UUID uuid){
        return albumes.values().stream()
                .anyMatch(a -> a.getUuid().equals(uuid));
    }

    @Override
    public Album save(Album album){
        log.info("Guardando álbum: {}", album);
        albumes.put(album.getId(), album);
        return album;
    }

    @Override
    public void deleteById(Long id){
        log.info("Borrando álbum por id: {}", id);
        albumes.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid){
        log.info("Borrando álbum por uuid: {}", uuid);
        albumes.values().removeIf(a -> a.getUuid().equals(uuid));
    }

    @Override
    public Long nextId(){
        return albumes.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;
    }
}
