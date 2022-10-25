package fi.dvv.digiid.ho.op.restservice.service.oidc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import fi.dvv.digiid.ho.common.jose.JWSVerifierFactory;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCAuthRequest;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCJwtPublicKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.text.ParseException;

@Slf4j
@Service
public class OIDCValidationService extends OIDCServiceBase {

    @Value("${fi.dvv.digiid.op.requireSignedAuthorize:true}")
    private boolean requireSignedAuthorize;

    @Value("${fi.dvv.digiid.op.oidcUrl}")
    private String oidcUrl;

    @Autowired
    public OIDCValidationService(OIDCConfiguration config, OIDCJwtPublicKeyRepository jwkCache, IdGenerator idGenerator) {
        super(config, jwkCache, idGenerator);
    }

    public OIDCConfiguration.OidcClient getAndValidateClient(OIDCAuthRequest request) throws OIDCAuthenticationBadRequestException {
        OIDCAuthRequest authRequest = getOidcAuthRequest(request);
        OIDCConfiguration.OidcClient foundClient = config.getOidcClientList().stream()
                .filter(client -> client.getClientId().equals(authRequest.getClientId()) && client.getRedirectUri().equals(authRequest.getRedirectUri()))
                .findFirst()
                .orElseThrow(() -> new OIDCAuthenticationBadRequestException("Client not found from client list"));

        if (requireSignedAuthorize) {
            try {
                JWK key = getKeyWithKeyUse(getKeys(foundClient), KeyUse.SIGNATURE);
                JWSVerifier verifier = JWSVerifierFactory.createVerifier(key);
                validateAuthRequest(request.getRequest(), verifier);
            } catch (JOSEException e) {
                throw new OIDCAuthenticationBadRequestException("Invalid request content");
            }
        }
        return foundClient;
    }

    private OIDCAuthRequest getOidcAuthRequest(OIDCAuthRequest request) throws OIDCAuthenticationBadRequestException {
        if (requireSignedAuthorize) {
            return parseAuthRequest(request.getRequest());
        }
        return request;
    }

    private OIDCAuthRequest parseAuthRequest(String request) throws OIDCAuthenticationBadRequestException {
        try {
            if (request == null) {
                throw new OIDCAuthenticationBadRequestException("Invalid client auth request");
            }

            JWSObject jwsObject = JWSObject.parse(request);
            String payload = jwsObject.getPayload().toString();

            OIDCAuthRequest authRequest = objectMapper.readValue(payload, OIDCAuthRequest.class);

            if (authRequest.getIss() == null || checkClientId(authRequest.getIss()).isEmpty()) {
                throw new OIDCAuthenticationBadRequestException("Invalid issuer or subject claim");
            }

            if (authRequest.getAud() == null || !authRequest.getAud().equals(oidcUrl)) {
                throw new OIDCAuthenticationBadRequestException("Invalid audience claim");
            }

            return authRequest;
        } catch (ParseException | JsonProcessingException e) {
            log.error("auth request failure", e);
            throw new OIDCAuthenticationBadRequestException("Invalid client auth request");
        }
    }

    private void validateAuthRequest(String request, JWSVerifier verifier) throws OIDCAuthenticationBadRequestException {
        try {
            JWSObject jwsObject = JWSObject.parse(request);
            if (!jwsObject.verify(verifier)) {
                throw new OIDCAuthenticationBadRequestException("AuthRequest validation error");
            }

        } catch (ParseException | JOSEException e) {
            throw new OIDCAuthenticationBadRequestException("Invalid request content");
        }
    }
}
