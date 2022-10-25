package fi.dvv.digiid.ho.op.restservice.service.oidc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCJwkPublicKey;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCJwtPublicKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.IdGenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class OIDCServiceBase {

    protected final OIDCConfiguration config;

    private final OIDCJwtPublicKeyRepository jwkCache;

    protected final ObjectMapper objectMapper;

    protected final IdGenerator idGenerator;

    protected OIDCServiceBase(OIDCConfiguration config, OIDCJwtPublicKeyRepository jwkCache, IdGenerator idGenerator) {
        this.config = config;
        this.jwkCache = jwkCache;
        this.idGenerator = idGenerator;
        this.objectMapper = new ObjectMapper();
    }

    protected Optional<OIDCConfiguration.OidcClient> checkClientId(String clientId) {
        return config.getOidcClientList().stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst();
    }

    protected JWKSet getKeys(OIDCConfiguration.OidcClient client) throws OIDCAuthenticationBadRequestException {
        try {
            String publicKey = client.getPublicKey();

            if (publicKey.startsWith("-----BEGIN CERTIFICATE-----")) { // locally stored PEM-file
                RSAKey clientKey = (RSAKey) JWK.parseFromPEMEncodedObjects(publicKey);
                return new JWKSet(clientKey);
            }

            // internet url
            Optional<OIDCJwkPublicKey> cached = jwkCache.findById(publicKey);
            if (cached.isPresent() && cached.get().getKeys() != null) {
                return JWKSet.parse(cached.get().getKeys());
            } else {
                // cache miss. Load and save to cache
                log.info("Loading keys from " + publicKey);
                JWKSet loadedKey = JWKSet.load(new URL(publicKey));
                jwkCache.save(new OIDCJwkPublicKey(publicKey, loadedKey.toString()));
                return loadedKey;
            }

        } catch (ParseException | JsonProcessingException | JOSEException | MalformedURLException e) {
            log.error("Error", e);
            throw new OIDCAuthenticationBadRequestException("Invalid client assertion content");
        } catch (IOException e) {
            log.error("Error", e);
            throw new OIDCAuthenticationBadRequestException("Key loading failed");
        }
    }

    protected JWK getKeyWithKeyUse(JWKSet publicKeys, KeyUse use) throws OIDCAuthenticationBadRequestException {
        List<JWK> matchWithSpecificUse = new JWKSelector(new JWKMatcher.Builder().keyUse(use).build())
                .select(publicKeys);
        if (matchWithSpecificUse.size() == 1) {
            return matchWithSpecificUse.get(0).toPublicJWK();
        }
        if (publicKeys.getKeys().size() == 1) {
            return publicKeys.getKeys().get(0).toPublicJWK();
        }
        throw new OIDCAuthenticationBadRequestException("No key with use " + use.getValue() + " found");
    }
}
