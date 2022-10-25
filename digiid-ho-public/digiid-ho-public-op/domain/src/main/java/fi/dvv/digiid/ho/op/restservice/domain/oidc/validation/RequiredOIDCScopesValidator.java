package fi.dvv.digiid.ho.op.restservice.domain.oidc.validation;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class RequiredOIDCScopesValidator implements ConstraintValidator<RequiredOIDCScopes, List<OIDCScope>> {

    private List<OIDCScope> requiredScopes;

    @Override
    public void initialize(RequiredOIDCScopes annotation) {
        this.requiredScopes = Arrays.asList(annotation.value());
    }

    @Override
    public boolean isValid(List<OIDCScope> list, ConstraintValidatorContext constraintValidatorContext) {
        return list != null && list.containsAll(requiredScopes);
    }
}
