package fi.dvv.digiid.ho.op.restservice.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoNullElementsValidator.class)
public @interface NoNullElements {

    String message() default "No null elements are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
