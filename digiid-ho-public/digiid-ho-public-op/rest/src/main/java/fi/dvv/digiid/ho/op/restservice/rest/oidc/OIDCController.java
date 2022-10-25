package fi.dvv.digiid.ho.op.restservice.rest.oidc;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import fi.dvv.digiid.ho.op.restservice.config.RestServiceConfiguration;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.*;
import fi.dvv.digiid.ho.op.restservice.rest.oidc.validator.ACRValidator;
import fi.dvv.digiid.ho.op.restservice.service.oidc.service.OIDCAuthorizationService;
import fi.dvv.digiid.ho.op.restservice.service.oidc.service.OIDCTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequestMapping(path = "/op")
@RestController
public class OIDCController {

    private final RestServiceConfiguration configuration;
    private final OIDCAuthorizationService authorizationService;
    private final OIDCTokenService tokenService;
    private final Validator modelValidator;
    private final ACRValidator acrValidator;
    private final RSAKey signingCertificate;

    private static final String ERROR_OCCURRED = "Error occurred";

    @Autowired
    public OIDCController(RestServiceConfiguration configuration, OIDCAuthorizationService authorizationService,
                          OIDCTokenService tokenService, ACRValidator acrValidator,
                          @Qualifier("signingCertificate") RSAKey signingCertificate) {
        this.configuration = configuration;
        this.authorizationService = authorizationService;
        this.tokenService = tokenService;
        this.acrValidator = acrValidator;
        this.modelValidator = Validation.buildDefaultValidatorFactory().getValidator();
        this.signingCertificate = signingCertificate;
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/authorize",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> authorize(String response_type, String client_id, String redirect_uri, String scope,
                                            String nonce, String state, String acr_values, String ui_locales, String ftn_spname, String request) throws URISyntaxException {
        try {
            OIDCAuthRequest authRequest = new OIDCAuthRequest();
            authRequest.setResponseType(response_type);
            authRequest.setClientId(client_id);
            authRequest.setRedirectUri(redirect_uri);
            authRequest.setScope(scope);
            authRequest.setNonce(nonce);
            authRequest.setState(state);
            authRequest.setUiLocales(ui_locales);
            authRequest.setFtnSpname(ftn_spname);
            authRequest.setRequest(request);
            authRequest.setAcrValues(acr_values);
            Set<ConstraintViolation<OIDCAuthRequest>> resp = modelValidator.validate(authRequest);

            acrValidator.validate(authRequest);

            if (resp.isEmpty()) {
                String uri = authorizationService.authorize(authRequest);

                return ResponseEntity.status(HttpStatus.FOUND).location(new URI(uri)).body(uri);
            } else {
                log.error("ConstraintViolation in OIDC authorize: " + resp);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (OIDCAuthenticationBadRequestException e) {
            log.error("OidController.authorize failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/cancel/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OIDCTransactionStatusResponse> cancel(@PathVariable String code, @RequestParam String error) {
        try {
            String url = authorizationService.cancel(code, error);
            return ResponseEntity.ok().body(new OIDCTransactionStatusResponse(OIDCTransactionStatus.FAILED, url));
        } catch (OIDCTransactionNotFoundException e) {
            log.error("OIDC Transaction not found", e);
            return ResponseEntity.notFound().build();
        } catch (OIDCAuthenticationBadRequestException e) {
            log.error("OIDC authentication failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/status/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OIDCTransactionStatusResponse> status(@PathVariable String code) {
        try {
            OIDCTransactionStatusResponse status = authorizationService.getStatus(code);
            return ResponseEntity.ok().body(new OIDCTransactionStatusResponse(status.getStatus(), status.getRedirect()));
        } catch (OIDCTransactionNotFoundException e) {
            log.error("OIDC Transaction not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping(path = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OIDCTokenResponse> token(@Valid @ModelAttribute OIDCTokenRequest tokenRequest) {
        try {
            return ResponseEntity.ok().body(tokenService.token(tokenRequest));
        } catch (OIDCAuthenticationBadRequestException | OIDCAuthenticationException e) {
            log.error(ERROR_OCCURRED, e);
            return ResponseEntity.badRequest().build();
        } catch (OIDCTransactionNotFoundException e) {
            log.error(ERROR_OCCURRED, e);
            return ResponseEntity.notFound().build();
        } catch (OIDCAuthenticationFailedException e) {
            log.error(ERROR_OCCURRED, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/.well-known/openid-configuration",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OIDCConfig> openIdConfiguration() {
        OIDCConfig oidcConfig = new OIDCConfig();

        oidcConfig.setIssuer(configuration.getOidcUrl());
        oidcConfig.setAuthorization_endpoint(configuration.getOidcUrl() + "/authorize");
        oidcConfig.setToken_endpoint(configuration.getOidcUrl() + "/token");
        oidcConfig.setJwks_uri(configuration.getOidcUrl() + "/jwks");

        return ResponseEntity.ok().body(oidcConfig);
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/jwks",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> jwks() {
        return ResponseEntity.ok().body(new JWKSet(signingCertificate).toJSONObject(true));
    }
}
