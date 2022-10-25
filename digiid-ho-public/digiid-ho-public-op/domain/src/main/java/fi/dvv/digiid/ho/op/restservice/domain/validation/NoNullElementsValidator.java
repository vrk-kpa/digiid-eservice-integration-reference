package fi.dvv.digiid.ho.op.restservice.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class NoNullElementsValidator implements ConstraintValidator<NoNullElements, List<?>> {

    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext constraintValidatorContext) {
        return list == null || list.stream().allMatch(Objects::nonNull);
    }
}
