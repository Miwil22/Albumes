package org.example.albumes.models;

import org.example.artistas.models.Artista;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// @Builder: Herramienta de Lombok para construir objetos de forma fluida (Album.builder()...build()).
@Builder
// @Getter: Lombok genera automáticamente los métodos getNombre(), getGenero(), etc.
@Getter
// @Setter: Lombok genera automáticamente los métodos setNombre(), setGenero(), etc.
@Setter
// @AllArgsConstructor: Lombok genera un constructor con todos los atributos de la clase.
@AllArgsConstructor
// @NoArgsConstructor: Lombok genera un constructor vacío. Es OBLIGATORIO para que JPA pueda instanciar la clase desde la base de datos.
@NoArgsConstructor
// @Entity: Marca esta clase como una Entidad JPA, lo que significa que se mapeará a una tabla en la base de datos.
@Entity
// @Table: Especifica el nombre de la tabla en la base de datos. En este caso, "ALBUMES".
@Table(name = "ALBUMES")
public class Album {
    // @Id: Indica que este campo es la Clave Primaria (PK) de la tabla.
    @Id
    // @GeneratedValue: Indica que el valor de este ID se genera automáticamente.
    // GenerationType.IDENTITY delegará en la base de datos (auto-increment) la creación del siguiente número.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false): Configura la columna en BD para que no acepte valores nulos (NOT NULL).
    // Si intentas guardar un álbum sin nombre, la base de datos rechazará la operación.
    @Column(nullable = false)
    private String nombre;

    // Columna obligatoria para el género.
    @Column(nullable = false)
    private String genero;

    // Columna obligatoria para el precio.
    @Column(nullable = false)
    private Float precio;

    // @Builder.Default: Asegura que Lombok use el valor por defecto (LocalDateTime.now()) al construir el objeto.
    @Builder.Default
    // updatable = false: Protege este campo. Una vez guardado, JPA nunca intentará actualizarlo en la BD.
    // La fecha de creación debe ser inmutable.
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Fecha de última actualización. Esta sí es actualizable por defecto.
    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // UUID: Identificador Universalmente Único.
    // Se usa a menudo como un ID público en las URLs para no exponer el ID numérico secuencial de la base de datos (seguridad por oscuridad).
    @Builder.Default
    // unique = true: Asegura que no haya dos álbumes con el mismo UUID.
    // updatable = false: Un UUID nunca debería cambiar una vez asignado.
    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid = UUID.randomUUID();

    // Campo para el Borrado Lógico.
    // false = Activo (visible). true = Borrado (oculto).
    // En lugar de DELETE, haremos UPDATE isDeleted = true.
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    // RELACIÓN MUCHOS A UNO (N:1).
    // Muchos Álbumes pueden pertenecer a un mismo Artista.
    @ManyToOne
    // @JoinColumn: Define cómo se llama la columna física en la tabla 'ALBUMES' que servirá de unión.
    // Aquí se guardará el ID del artista (Clave Foránea / Foreign Key).
    @JoinColumn(name = "artista_id")
    private Artista artista;
}