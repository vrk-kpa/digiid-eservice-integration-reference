package fi.dvv.digiid.ho.op.restservice.domain.siop.validation;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidSIOPScopesValidator implements ConstraintValidator<ValidSIOPScopes, List<SIOPScope>> {

    private List<SIOPScope> validScopes;

    @Override
    public void initialize(ValidSIOPScopes annotation) {
        this.validScopes = Arrays.asList(annotation.value());
    }

    @Override
    public boolean isValid(List<SIOPScope> scopes, ConstraintValidatorContext constraintValidatorContext) {
        if (scopes != null && !scopes.isEmpty()) {
            for (SIOPScope scope : scopes) {
                if (!validScopes.contains(scope)) {
                    return false;
                }
            }
        }
        return true;
    }
}
