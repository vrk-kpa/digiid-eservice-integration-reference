package fi.dvv.digiid.ho.op.restservice.service.siop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import fi.dvv.digiid.ho.common.jsonld.JsonLdUtil;
import fi.dvv.digiid.ho.common.jsonld.SpringDocumentLoader;
import fi.dvv.digiid.ho.common.vc.Proof;
import fi.dvv.digiid.ho.common.vc.Verifiable;
import fi.dvv.digiid.ho.common.vc.VerifiableCredential;
import fi.dvv.digiid.ho.common.vc.VerifiablePresentation;
import fi.dvv.digiid.ho.common.vc.did.DidDocumentDto;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPAuthRequest;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPCredential;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPIdToken;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPTransactionStatus;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransactionRepository;
import fi.dvv.digiid.ho.vdr.repo.VdrClient;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.LdProof;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
public class SIOPAuthenticationService extends SIOPServiceBase {
    private final VdrClient vdrClient;
    private final SpringDocumentLoader documentLoader;
    private final Clock clock;
    private final JsonSchema idTokenSchema;
    private final JsonSchema vpTokenSchema;

    private static final String JSON_PROCESSING_FAILED = "JSON processing failed";

    @Autowired
    public SIOPAuthenticationService(SIOPTransactionRepository transactionRepository, VdrClient vdrClient,
                                     SpringDocumentLoader documentLoader, Clock clock) throws IOException {
        super(transactionRepository);
        this.vdrClient = vdrClient;
        this.documentLoader = documentLoader;
        this.clock = clock;
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource idResource = resourceLoader.getResource("classpath:digiid-id-token-schema-1.0.json");
        this.idTokenSchema = getJsonSchema(asString(idResource));
        Resource resource = resourceLoader.getResource("classpath:digiid-core-schema-1.0.json");
        this.vpTokenSchema = getJsonSchema(asString(resource));
    }

    public SIOPTransaction handleAuthError(SIOPAuthRequest request) throws SIOPTransactionNotFoundException, SIOPAuthenticationFailedException, SIOPAuthenticationBadRequestException {
        SIOPTransaction transaction = findCreatedTransaction(request.getState());
        transaction.setStatus(SIOPTransactionStatus.FAILED);
        transaction.setError(request.getError());
        transactionRepository.save(transaction);
        return transaction;
    }

    public void handleAuth(SIOPAuthRequest request) throws SIOPTransactionNotFoundException,
            SIOPAuthenticationException, SIOPAuthenticationFailedException, SIOPAuthenticationBadRequestException {
        SIOPTransaction transaction = null;
        try {
            VerifiablePresentation vpToken = readAndValidateVpToken(request.getVpToken());
            transaction = findCreatedTransaction(vpToken.getProof().getChallenge());

            JWSObject parsedIdToken = JWSObject.parse(request.getIdToken());

            validateTokens(vpToken, parsedIdToken);

            List<SIOPCredential> credentials = getValidatedCredentials(vpToken);
            transaction.setCredentials(credentials);
            transaction.setStatus(SIOPTransactionStatus.READY);

        } catch (JOSEException | ParseException ex) {
            if (transaction != null) {
                transaction.setStatus(SIOPTransactionStatus.FAILED);
            }
            throw new SIOPAuthenticationException(JSON_PROCESSING_FAILED, ex);
        } catch (SIOPAuthenticationException | SIOPTransactionNotFoundException |
                 SIOPAuthenticationFailedException | SIOPAuthenticationBadRequestException ex) {
            log.error("Handling authentication failed", ex);
            if (transaction != null) {
                transaction.setStatus(SIOPTransactionStatus.FAILED);
            }
            throw ex;
        } finally {
            if (transaction != null) {
                transactionRepository.save(transaction);
            }
        }
    }

    private void validateTokens(VerifiablePresentation vpToken, JWSObject parsedIdToken) throws SIOPAuthenticationBadRequestException, SIOPAuthenticationException, JOSEException, SIOPAuthenticationFailedException {

        JWSVerifier verifier = getHytVerifier(vpToken);

        if (!parsedIdToken.verify(verifier)) {
            throw new SIOPAuthenticationFailedException("Id token validation failed");
        }

        if (!verifySignature(vpToken, verifier)) {
            throw new SIOPAuthenticationFailedException("Vp token validation failed");
        }

        SIOPIdToken idToken = readAndValidateIdToken(parsedIdToken.getPayload().toString());

        String idTokenSub = idToken.getSub();
        if (!idTokenSub.contains("hyt:")) {
            throw new SIOPAuthenticationBadRequestException("Authentication failed: No hyt found from request idToken");
        }
        if (!idTokenSub.equals(vpToken.getHolder())) {
            throw new SIOPAuthenticationBadRequestException("Authentication failed: Id token subject doesn't match VP holder");
        }
    }

    public VerifiablePresentation readAndValidateVpToken(String token) throws SIOPAuthenticationException, SIOPAuthenticationBadRequestException {
        return readAndValidateToken(token, vpTokenSchema, VerifiablePresentation.class);
    }

    public SIOPIdToken readAndValidateIdToken(String token) throws SIOPAuthenticationException, SIOPAuthenticationBadRequestException {
        return readAndValidateToken(token, idTokenSchema, SIOPIdToken.class);
    }

    private <T> T readAndValidateToken(String token, JsonSchema schema, Class<T> type) throws SIOPAuthenticationException, SIOPAuthenticationBadRequestException {
        try {
            JsonNode json = objectMapper.readTree(token);
            Set<ValidationMessage> validationMessages = schema.validate(json);
            if (!validationMessages.isEmpty()) {
                throw new SIOPAuthenticationBadRequestException(validationMessages.toString());
            }
            return objectMapper.readValue(token, type);
        } catch (JsonProcessingException e) {
            throw new SIOPAuthenticationException("Invalid token content", e);
        }
    }

    private JWSVerifier getHytVerifier(VerifiablePresentation vpToken) throws SIOPAuthenticationException {
        String hyt = vpToken.getProof().getVerificationMethod().toString();
        DidDocumentDto crt = vdrClient.getCertByDid(hyt)
                .blockOptional()
                .orElseThrow(() -> new SIOPAuthenticationException("Certificate not found from VDR"));
        URI keyId = vpToken.getProof().getVerificationMethod();

        return VerifierFactory.getVerifier(crt, keyId);
    }

    private JWSVerifier getIssuerVerifier(URI issuer) throws SIOPAuthenticationException {

        DidDocumentDto crt = vdrClient.getCertByDid(issuer.toString())
                .blockOptional()
                .orElseThrow(() -> new SIOPAuthenticationException("Certificate not found from VDR"));

        return VerifierFactory.getVerifier(crt, issuer);
    }


    private List<SIOPCredential> getValidatedCredentials(VerifiablePresentation verifiablePresentation) throws SIOPAuthenticationException {

        List<SIOPCredential> credentials = new ArrayList<>();

        Map<String, JWSVerifier> verifierCache = new HashMap<>();
        for (VerifiableCredential verifiable : verifiablePresentation.getVerifiableCredentials()) {
            if (!validateVerifiableCredential(verifiable, verifiablePresentation)) {
                continue;
            }

            JWSVerifier verifier = getJwsVerifierAndUpdateCache(verifierCache, verifiable);

            boolean validCredentialSignature = verifySignature(verifiable, verifier);
            boolean expiredCredential = isCredentialExpired(verifiable);

            SIOPCredential credential = new SIOPCredential();
            credential.setValidationStatus(validCredentialSignature && !expiredCredential);

            Set<String> keys = verifiable.getCredentialSubject().keySet();
            keys.stream()
                    .filter(key -> !key.equals("id"))
                    .forEach(key -> {
                        Object value = verifiable.getCredentialSubject().get(key);

                        if (valueIsNonEmptyListOfStrings(value)) {
                            // LoA values are lists of strings - convert them to space separated string
                            String joined = ((List<?>) value).stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(" "));
                            credential.setValue(joined);
                        } else {
                            credential.setValue(value);
                        }
                        credential.setCredential(key);
                        credentials.add(credential);
                    });
        }

        return credentials;
    }

    private boolean validateVerifiableCredential(VerifiableCredential verifiable, VerifiablePresentation vp) {
        if (verifiable.getCredentialSubject() == null) {
            log.warn("Credential has no subject");
            return false;
        }
        if (!isCredentialSubjectTokenHolder(verifiable, vp)) {
            log.warn("Credential subject does not match token holder");
            return false;
        }
        return true;
    }

    private JWSVerifier getJwsVerifierAndUpdateCache(Map<String, JWSVerifier> verifierCache, VerifiableCredential verifiable) throws SIOPAuthenticationException {
        URI cacheKey = verifiable.getProof().getVerificationMethod();
        JWSVerifier verifier = verifierCache.get(cacheKey.toString());

        if (verifier == null) {
            verifier = getIssuerVerifier(cacheKey);
            verifierCache.put(cacheKey.toString(), verifier);
        }
        return verifier;
    }

    private boolean valueIsNonEmptyListOfStrings(Object value) {
        if (!(value instanceof List<?> convertedValue)) {
            return false;
        }
        return !convertedValue.isEmpty() && convertedValue.get(0) instanceof String;
    }

    private boolean verifySignature(Verifiable<? extends Proof> verifiable, JWSVerifier verifier) throws SIOPAuthenticationException {
        try {
            JsonLDObject jsonLdObject = JsonLDObject.fromJson(objectMapper.writeValueAsString(verifiable));
            jsonLdObject.setDocumentLoader(documentLoader);
            LdProof ldProof = LdProof.getFromJsonLDObject(jsonLdObject);
            byte[] canonicalizationResult = JsonLdUtil.canonicalize(ldProof, jsonLdObject);

            return JsonLdUtil.verifySignatureJwk(
                    canonicalizationResult,
                    ldProof.getJws(),
                    verifier);
        } catch (JOSEException | IOException | GeneralSecurityException | JsonLDException | ParseException ex) {
            throw new SIOPAuthenticationException(JSON_PROCESSING_FAILED, ex);
        }
    }

    private static String asString(Resource resource) throws IOException {
        Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8);
        return FileCopyUtils.copyToString(reader);
    }

    private static JsonSchema getJsonSchema(String schemaJson) {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        InputStream schemaStream = new ByteArrayInputStream(schemaJson.getBytes());
        return jsonSchemaFactory.getSchema(schemaStream);
    }

    private boolean isCredentialExpired(VerifiableCredential credential) {
        boolean expired = !credential.getExpirationDate().after(new Date(clock.millis()));
        if (expired) {
            log.warn("Credential expired");
        }
        return expired;
    }

    private static boolean isCredentialSubjectTokenHolder(VerifiableCredential verifiable, VerifiablePresentation validToken) {
        return StringUtils.equals(validToken.getHolder(), verifiable.getCredentialSubject().get("id").toString());
    }
}
