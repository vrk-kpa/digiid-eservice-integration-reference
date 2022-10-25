package fi.dvv.digiid.ho.op.restservice.service.oidc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.*;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPCredential;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.text.ParseException;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OIDCTokenService extends OIDCServiceBase {

    private static final long TOKEN_EXPIRATION_TIME = 600;

    @Value("${fi.dvv.digiid.op.oidcUrl}")
    private String oidcUrl;

    private final OIDCTransactionRepository transactionRepository;
    private final OIDCJwtIdRepository jwtIdCache;
    private final Clock clock;
    private final RSAKey signingKey;
    private final AuthenticationContextClassReferenceMapper acrMapper;

    @Autowired
    public OIDCTokenService(OIDCConfiguration config, OIDCTransactionRepository transactionRepository, OIDCJwtIdRepository jwtIdCache,
                            OIDCJwtPublicKeyRepository jwkCache, Clock clock, @Qualifier("signingKey") RSAKey signingKey,
                            AuthenticationContextClassReferenceMapper acrMapper, IdGenerator idGenerator) {
        super(config, jwkCache, idGenerator);
        this.transactionRepository = transactionRepository;
        this.jwtIdCache = jwtIdCache;
        this.clock = clock;
        this.signingKey = signingKey;
        this.acrMapper = acrMapper;
    }

    public OIDCTokenResponse token(OIDCTokenRequest request) throws OIDCAuthenticationException, OIDCAuthenticationBadRequestException, OIDCTransactionNotFoundException, OIDCAuthenticationFailedException {
        try {
            OIDCTransaction transaction = transactionRepository
                    .findById(request.getCode())
                    .orElseThrow(OIDCTransactionNotFoundException::new);

            if (transaction.getCredentials() == null || transaction.getCredentials().isEmpty()) {
                throw new OIDCAuthenticationBadRequestException("No credentials found from OIDC transaction");
            }

            JWKSet keys = getKeys(transaction.getClient());
            JWK signKey = getKeyWithKeyUse(keys, KeyUse.SIGNATURE);

            OIDCClientAssertion clientAssertion = parseAndValidateClientAssertion(signKey, request.getClientAssertion());

            if (!transaction.getClient().getClientId().equals(clientAssertion.getIss())) {
                throw new OIDCAuthenticationBadRequestException("Invalid client");
            }

            switch (transaction.getStatus()) {
                case FAILED -> throw new OIDCAuthenticationFailedException("Authentication failed");
                case CREATED -> throw new OIDCAuthenticationFailedException("Authentication not finished");
                case READY -> transactionRepository.delete(transaction);
            }

            AuthenticationContextClassReference fulfilledAcr = validateLoACredentialAndGetFulfilledAcr(transaction);

            jwtIdCache.save(OIDCJwtId.builder().jti(clientAssertion.getJti()).build());

            JWEObject jweToken = generateJweToken(transaction, getKeyWithKeyUse(keys, KeyUse.ENCRYPTION), fulfilledAcr);
            return OIDCTokenResponse.builder()
                    .accessToken(UUID.randomUUID().toString())
                    .idToken(jweToken.serialize())
                    .expiresIn(clock.millis() / 1000 + TOKEN_EXPIRATION_TIME)
                    .build();
        } catch (JOSEException e) {
            log.error("Error", e);
            throw new OIDCAuthenticationException(e);
        }
    }

    private AuthenticationContextClassReference validateLoACredentialAndGetFulfilledAcr(OIDCTransaction transaction) throws OIDCAuthenticationFailedException {
        List<AuthenticationContextClassReference> requestedAcrs = transaction.getAcr();
        SIOPCredential levelOfAssuranceCredential = transaction.getCredentials().stream()
                .filter(credential -> credential.getCredential().equals("level_of_assurance"))
                .findFirst()
                .orElseThrow(() -> new OIDCAuthenticationFailedException("Required LoA credential not found"));
        return getFulfilledAcr(requestedAcrs, levelOfAssuranceCredential);
    }

    private AuthenticationContextClassReference getFulfilledAcr(List<AuthenticationContextClassReference> requestedAcrs, SIOPCredential levelOfAssuranceCredential) throws OIDCAuthenticationFailedException {
        List<AuthenticationContextClassReference> receivedAcrs = acrMapper.mapLoAToACR(levelOfAssuranceCredential.getValue().toString());
        List<AuthenticationContextClassReference> fulfilledAcrs = getOverlapBetweenLists(requestedAcrs, receivedAcrs);
        if (fulfilledAcrs.isEmpty()) {
            throw new OIDCAuthenticationFailedException("Unable to fulfil requested acr");
        }
        if (fulfilledAcrs.size() > 1) {
            throw new OIDCAuthenticationFailedException("Fulfilled more than one ACR: " + fulfilledAcrs);
        }
        return fulfilledAcrs.get(0);
    }

    private List<AuthenticationContextClassReference> getOverlapBetweenLists(List<AuthenticationContextClassReference> list1, List<AuthenticationContextClassReference> list2) {
        return list1.stream()
                .distinct()
                .filter(list2::contains)
                .toList();
    }

    private OIDCClientAssertion parseAndValidateClientAssertion(JWK key, String clientAssertionStr) throws
            OIDCAuthenticationBadRequestException {
        try {
            JWSObject jwsObject = JWSObject.parse(clientAssertionStr);
            JWSVerifier verifier = new RSASSAVerifier((RSAKey) key);

            if (!jwsObject.verify(verifier)) {
                throw new OIDCAuthenticationBadRequestException("Token validation error");
            }

            String payload = jwsObject.getPayload().toString();
            OIDCClientAssertion clientAssertion = objectMapper.readValue(payload, OIDCClientAssertion.class);

            if (clientAssertion.getIss() == null
                    || clientAssertion.getSub() == null
                    || !clientAssertion.getIss().equals(clientAssertion.getSub())
                    || checkClientId(clientAssertion.getIss()).isEmpty()) {
                throw new OIDCAuthenticationBadRequestException("Invalid issuer or subject claim");
            }

            if (clientAssertion.getAud() == null || !clientAssertion.getAud().contains(oidcUrl + "/token")) {
                throw new OIDCAuthenticationBadRequestException("Invalid audience claim");
            }

            if (jwtIdCache.findById(clientAssertion.getJti()).isPresent()) {
                throw new OIDCAuthenticationBadRequestException("JWT Id claim not unique");
            }

            long diff = clientAssertion.getExp() - clock.millis() / 1000;
            if (diff < 0 || diff > TOKEN_EXPIRATION_TIME) {
                throw new OIDCAuthenticationBadRequestException("Invalid expiration claim");
            }

            return clientAssertion;
        } catch (ParseException | JsonProcessingException | JOSEException e) {
            log.error("Client assertion failure", e);
            throw new OIDCAuthenticationBadRequestException("Invalid client assertion");
        }
    }

    private JWEObject generateJweToken(OIDCTransaction transaction, JWK encryptKey, AuthenticationContextClassReference fulfilledAcr) throws JOSEException {
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .issuer(oidcUrl)
                .subject(UUID.randomUUID().toString())
                .audience(transaction.getClient().getClientId())
                .issueTime(new Date(clock.millis()))
                .expirationTime(new Date(clock.millis() + TOKEN_EXPIRATION_TIME * 1000));

        claims.claim("auth_time", transaction.getTimestamp() / 1000);
        claims.claim("nonce", transaction.getNonce());
        claims.claim("acr", fulfilledAcr.getUri());

        String firstName = null;
        String lastName = null;
        for (SIOPCredential cred : transaction.getCredentials()) {
            switch (cred.getCredential()) {
                case "given_name" -> {
                    claims.claim("given_name", cred.getValue());
                    firstName = cred.getValue().toString();
                }
                case "family_name" -> {
                    claims.claim("family_name", cred.getValue());
                    lastName = cred.getValue().toString();
                }
                case "birth_date" -> claims.claim("birthdate", cred.getValue().toString());
                case "personal_identity_code" -> claims.claim(cred.getCredential(), cred.getValue());
                default -> {
                    // noop
                }
            }
        }

        if (lastName != null && firstName != null) {
            claims.claim("name", lastName + " " + firstName);
        }

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingKey.getKeyID()).build(),
                claims.build());
        signedJWT.sign(new RSASSASigner(signingKey));
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                        .contentType("JWT")
                        .build(),
                new Payload(signedJWT));

        jweObject.encrypt(new RSAEncrypter((RSAKey) encryptKey));
        return jweObject;
    }
}
