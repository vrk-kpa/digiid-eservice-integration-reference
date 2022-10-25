package fi.dvv.digiid.ho.op.restservice.domain.siop.validation;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPScope;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSIOPScopesValidator.class)
public @interface ValidSIOPScopes {

    SIOPScope[] value();

    String message() default "The following scopes are valid...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
