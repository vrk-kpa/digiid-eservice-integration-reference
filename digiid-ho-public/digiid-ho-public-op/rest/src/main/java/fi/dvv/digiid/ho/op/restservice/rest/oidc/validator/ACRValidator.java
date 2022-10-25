package fi.dvv.digiid.ho.op.restservice.rest.oidc.validator;

import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCAuthRequest;

@FunctionalInterface
public interface ACRValidator {
    void validate(OIDCAuthRequest authRequest) throws OIDCAuthenticationBadRequestException;
}
