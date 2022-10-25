package fi.dvv.digiid.ho.op.restservice.domain.siop.validation;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPClaim;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSIOPClaimsValidator.class)
public @interface ValidSIOPClaims {

    SIOPClaim[] value();

    String message() default "The following claims are valid...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
