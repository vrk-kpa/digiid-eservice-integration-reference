package fi.dvv.digiid.ho.op.restservice.domain.siop.validation;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class RequiredSIOPScopesValidator implements ConstraintValidator<RequiredSIOPScopes, List<SIOPScope>> {

    private List<SIOPScope> requiredScopes;

    @Override
    public void initialize(RequiredSIOPScopes annotation) {
        this.requiredScopes = Arrays.asList(annotation.value());
    }

    @Override
    public boolean isValid(List<SIOPScope> list, ConstraintValidatorContext constraintValidatorContext) {
        return list != null && list.containsAll(requiredScopes);
    }
}
