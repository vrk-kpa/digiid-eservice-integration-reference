package fi.dvv.digiid.ho.op.restservice.domain.oidc.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MustEqualValidator implements ConstraintValidator<MustEqual, String> {

    private String allowedValue;

    @Override
    public void initialize(MustEqual annotation) {
        this.allowedValue = annotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return allowedValue.equals(value);
    }
}
