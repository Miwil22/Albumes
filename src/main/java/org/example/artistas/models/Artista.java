package org.example.artistas.models;

import org.example.albumes.models.Album;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// @Builder: Es una herramienta de Lombok que nos permite crear objetos de esta clase de forma más legible.
// En lugar de `new Artista("Queen", ...)`, podremos hacer `Artista.builder().nombre("Queen").build()`.
@Builder
// @Getter: Lombok genera automáticamente el código de los métodos 'get' para todos los campos (getNombre, getId, etc.).
@Getter
// @Setter: Lombok genera automáticamente el código de los métodos 'set' (setNombre, setId, etc.) para poder modificar los valores.
@Setter
// @AllArgsConstructor: Lombok crea un constructor que recibe TODOS los campos como parámetros.
@AllArgsConstructor
// @NoArgsConstructor: Lombok crea un constructor vacío (sin parámetros).
// Esto es OBLIGATORIO para que JPA (la base de datos) pueda funcionar, ya que necesita crear instancias vacías antes de rellenarlas.
@NoArgsConstructor
// @Entity: Esta es la anotación más importante. Le dice a Spring que esta clase NO es una clase normal,
// sino que representa una TABLA en la base de datos. Cada objeto 'Artista' será una fila en esa tabla.
@Entity
// @Table: Sirve para especificar el nombre exacto que tendrá la tabla en la base de datos.
// Si no lo ponemos, usaría el nombre de la clase, pero es buena práctica ponerlo en mayúsculas y plural ("ARTISTAS").
@Table(name = "ARTISTAS")
public class Artista {
    // @Id: Indica que este campo es la CLAVE PRIMARIA (Primary Key) de la tabla. Es el identificador único.
    @Id
    // @GeneratedValue: Le dice a la base de datos cómo generar este ID.
    // strategy = GenerationType.IDENTITY significa que la base de datos se encargará de autoincrementarlo (1, 2, 3...) automáticamente.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column: Configura detalles específicos de esta columna en la tabla.
    // unique = true: Impide que se guarden dos artistas con el mismo nombre. Si lo intentas, la base de datos dará error.
    // nullable = false: Hace que este campo sea OBLIGATORIO (NOT NULL). No se puede guardar un artista sin nombre.
    @Column(unique = true, nullable = false)
    private String nombre;

    // @Builder.Default: Cuando usamos el patrón Builder (ver arriba), los campos toman su valor por defecto de Java (null para objetos).
    // Esta anotación obliga a Lombok a usar el valor que asignamos aquí (= LocalDateTime.now()) en lugar de null.
    @Builder.Default
    // updatable = false: Significa que una vez se guarda el registro, esta columna NUNCA se actualizará en la base de datos,
    // aunque cambies el valor en el objeto Java. Es útil para fechas de creación.
    // columnDefinition: Inyecta SQL directo al crear la tabla para definir un valor por defecto a nivel de base de datos.
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Similar al anterior, pero sin 'updatable = false', porque la fecha de actualización SÍ queremos que cambie.
    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Esto es para el "Borrado Lógico" (Soft Delete).
    // En lugar de eliminar la fila con un DELETE (que pierde el dato para siempre), marcaremos este campo como TRUE.
    // columnDefinition define que en la base de datos será un booleano con valor 'false' por defecto.
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    // RELACIÓN UNO A MUCHOS (1:N).
    // Un (1) Artista puede tener Muchos (N) Álbumes asociados.
    // mappedBy = "artista": Esto es crucial. Le dice a JPA: "No crees una tabla intermedia. La relación ya está definida
    // en el otro lado (en la clase Album), en el campo que se llama 'artista'". El dueño de la relación es el Álbum.
    @OneToMany(mappedBy = "artista")
    // @JsonIgnoreProperties: Problema de recursividad.
    // Cuando pidamos un Artista, este vendrá con su lista de álbumes. Pero cada álbum tiene un campo 'artista'.
    // Si no paramos esto, Java intentará pintar Artista -> Album -> Artista -> Album... hasta el infinito.
    // Esta anotación dice: "Cuando pintes los objetos de esta lista 'albumes', IGNORA su campo 'artista'".
    @JsonIgnoreProperties("artista") // Para evitar bucles infinitos al convertir a JSON
    private List<Album> albumes;
}