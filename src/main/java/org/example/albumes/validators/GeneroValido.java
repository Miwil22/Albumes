package org.example.albumes.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
// @Constraint: Aquí conectamos la anotación con la clase que tiene la lógica (el validador).
@Constraint(validatedBy = GeneroValidoValidator.class)
// @Target: Define dónde se puede poner esta anotación (FIELD = encima de atributos/variables).
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneroValido {
    // El mensaje de error por defecto que saldrá si la validación falla.
    String message() default "El género no es válido. Debe ser Rock o Pop";

    // Grupos de validación (avanzado, se suele dejar por defecto).
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}