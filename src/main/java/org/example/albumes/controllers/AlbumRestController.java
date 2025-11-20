package org.example.albumes.controllers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadRequestException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.services.AlbumService;
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

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * Usamos el repositorio de productos y lo inyectamos en el constructor con Autowired
 *
 * @RequiredArgsConstructor es una anotación Lombok que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring
 * con solo declarar las dependencias como final ya que el constructor lo genera Lombok
 */
@Slf4j // Permite logs
@RequiredArgsConstructor // Inyección de dependencias automática
@RestController // Indica que esta clase maneja peticiones web y sus respuestas son datos JSON (no HTML).
@RequestMapping("api/${api.version}/albumes") // Define la URL base para todos los métodos (ej: localhost:3000/api/v1/albumes)
public class AlbumRestController {
    // Servicio de álbumes. El controlador NUNCA debe hablar con el repositorio, solo con el servicio.
    private final AlbumService albumService;

    /**
     * Obtiene todos los álbumes
     *
     * @param nombre    Nombre del álbum (parámetro opcional en la URL ?nombre=...)
     * @param artista   Artista del álbum (parámetro opcional en la URL ?artista=...)
     * @return Lista de álbumes y código 200 OK
     */
    @GetMapping() // Mapea peticiones HTTP GET a esta función.
    public ResponseEntity<List<AlbumResponseDto>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String artista) {
        log.info("Buscando álbumes por nombre={}, artista={}", nombre, artista);
        // Llama al servicio y envuelve la lista en un ResponseEntity con estado OK.
        return ResponseEntity.ok(albumService.findAll(nombre, artista));
    }

    /**
     * Obtiene un álbum por su id
     *
     * @param id del álbum, se pasa como PARTE de la ruta (ej: /albumes/5)
     * @return AlbumResponseDto si existe
     * @throws AlbumNotFoundException si no existe el álbum (404) (gestionado automáticamente por la excepción)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando álbum por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    /**
     * Crear un álbum
     *
     * @param createDto Datos para crear, vienen en el CUERPO (Body) de la petición en formato JSON.
     * @return AlbumResponseDto creado y código 201 CREATED.
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PostMapping() // Mapea peticiones HTTP POST
    public ResponseEntity<AlbumResponseDto> create(
            // @Valid: Activa las validaciones que pusimos en el DTO (@NotBlank, @Positive...).
            // Si fallan, lanza una excepción antes de ejecutar el código del método.
            @Valid @RequestBody AlbumCreateDto createDto) {
        log.info("Creando álbum : {}", createDto);
        var saved = albumService.save(createDto);
        // Devolvemos status 201 (Created) que es lo correcto al crear recursos.
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    /**
     * Actualiza un álbum
     *
     * @param id      del álbum a actualizar (en la ruta)
     * @param updateDto con los datos a actualizar (en el cuerpo JSON)
     * @return AlbumResponseDto actualizado
     * @throws AlbumNotFoundException si no existe el álbum (404)
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PutMapping("/{id}") // Mapea peticiones HTTP PUT (actualización completa)
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum id={} con álbum={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    /**
     * Actualiza parcialmente un álbum
     *
     * @param id      del álbum a actualizar
     * @param updateDto con los datos a actualizar
     * @return Álbum actualizado
     * @throws AlbumNotFoundException si no existe el álbum (404)
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PatchMapping("/{id}") // Mapea peticiones HTTP PATCH (actualización parcial)
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando parcialmente álbum con id={} con álbum={}",id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    /**
     * Borra un álbum por su id
     *
     * @param id del álbum a borrar
     * @return ResponseEntity con status 204 No Content (éxito pero sin cuerpo)
     * @throws AlbumNotFoundException si no existe el álbum (404)
     */
    @DeleteMapping("/{id}") // Mapea peticiones HTTP DELETE
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando álbum por id: {}", id);
        albumService.deleteById(id);
        // 204 No Content es el estándar para un borrado exitoso.
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     * Este método captura automáticamente los errores lanzados por @Valid.
     *
     * @param ex La excepción que contiene la lista de errores de validación.
     * @return Un objeto 'ProblemDetail' (JSON estándar de error) con la lista de campos fallidos.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Forzamos que la respuesta sea un 400 Bad Request.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        // Recorremos los errores y creamos un mapa "Campo" -> "Mensaje de error"
        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        // Añadimos el mapa de errores a la respuesta JSON
        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}