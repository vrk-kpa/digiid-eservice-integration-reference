package fi.dvv.digiid.ho.op.restservice.domain.oidc.validation;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCScope;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredOIDCScopesValidator.class)
public @interface RequiredOIDCScopes {

    OIDCScope[] value();

    String message() default "The following scopes are required...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
