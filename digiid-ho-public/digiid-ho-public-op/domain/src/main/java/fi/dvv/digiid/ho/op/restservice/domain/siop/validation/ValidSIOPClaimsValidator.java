package fi.dvv.digiid.ho.op.restservice.domain.siop.validation;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPClaim;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidSIOPClaimsValidator implements ConstraintValidator<ValidSIOPClaims, List<SIOPClaim>> {

    private List<SIOPClaim> validClaims;

    @Override
    public void initialize(ValidSIOPClaims annotation) {
        this.validClaims = Arrays.asList(annotation.value());
    }

    @Override
    public boolean isValid(List<SIOPClaim> claims, ConstraintValidatorContext constraintValidatorContext) {
        if (claims != null && !claims.isEmpty()) {
            for (SIOPClaim claim : claims) {
                if (!validClaims.contains(claim)) {
                    return false;
                }
            }
        }
        return true;
    }
}
