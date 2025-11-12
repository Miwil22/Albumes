package albumes.controllers;

import albumes.dto.AlbumCreateDto;
import albumes.dto.AlbumResponseDto;
import albumes.dto.AlbumUpdateDto;
import albumes.services.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AlbumRestController {

    private final AlbumService albumService;

    @GetMapping()
    public ResponseEntity<List<AlbumResponseDto>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String artista) {
        log.info("Buscando álbumes por nombre={}, artista={}", nombre, artista);
        return ResponseEntity.ok(albumService.findAll(nombre, artista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando álbum por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto createDto) {
        log.info("Creando álbum: {}", createDto);
        var saved = albumService.save(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum id={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id,
                                                          @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando parcialmente álbum id={} con álbum={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando álbum por id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidException(
            MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "' " + "Número de errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }

}


