package albumes.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class GeneroValidoValidator implements ConstraintValidator<GeneroValido, String> {

    private static final Set<String> GENEROS_PERMITIDOS = Set.of("Rock", "Pop");

    @Override
    public void initialize(GeneroValido constraintAnnotation){
    }

    @Override
    public boolean isValid(String generoField, ConstraintValidatorContext context){
        if(generoField == null || generoField.isBlank()){
            return true;
        }
        return GENEROS_PERMITIDOS.stream()
                .anyMatch(g -> g.equalsIgnoreCase(generoField));
    }
}
