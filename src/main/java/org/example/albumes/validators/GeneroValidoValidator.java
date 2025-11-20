package org.example.albumes.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

// Implementa la interfaz ConstraintValidator<Anotación, TipoDeDatoAValidar>
public class GeneroValidoValidator implements ConstraintValidator<GeneroValido, String> {

    // Definimos una lista inmutable con los géneros que aceptamos.
    private static final Set<String> GENEROS_PERMITIDOS = Set.of("Rock", "Pop");

    @Override
    public void initialize(GeneroValido constraintAnnotation){
        // Método de inicio, normalmente se deja vacío salvo que la anotación tenga parámetros extra.
    }

    // ESTE ES EL MÉTODO CLAVE.
    // Recibe el valor del campo (generoField) y devuelve true (válido) o false (error).
    @Override
    public boolean isValid(String generoField, ConstraintValidatorContext context){
        // Si es nulo o vacío, devolvemos true.
        // ¿Por qué? Porque esta validación solo comprueba el CONTENIDO.
        // Si queremos que sea obligatorio, usaremos @NotBlank junto a esta.
        if(generoField == null || generoField.isBlank()){
            return true;
        }
        // Comprobamos si el texto está en nuestra lista de permitidos (ignorando mayúsculas/minúsculas).
        // Rock, rock, ROCK -> Todos valen.
        return GENEROS_PERMITIDOS.stream()
                .anyMatch(g -> g.equalsIgnoreCase(generoField));
    }
}