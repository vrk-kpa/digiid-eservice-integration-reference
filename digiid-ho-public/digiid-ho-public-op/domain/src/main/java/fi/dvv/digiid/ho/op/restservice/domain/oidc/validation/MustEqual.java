package fi.dvv.digiid.ho.op.restservice.domain.oidc.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MustEqualValidator.class)
public @interface MustEqual {

    String value();

    String message() default "Field value must equal...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
