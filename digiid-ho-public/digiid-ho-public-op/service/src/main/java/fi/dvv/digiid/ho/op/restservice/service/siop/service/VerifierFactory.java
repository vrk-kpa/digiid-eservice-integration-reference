package fi.dvv.digiid.ho.op.restservice.service.siop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import fi.dvv.digiid.ho.common.jose.JWSVerifierFactory;
import fi.dvv.digiid.ho.common.vc.did.DidDocumentDto;
import fi.dvv.digiid.ho.common.vc.did.VerificationMethodDto;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationException;

import java.net.URI;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

public class VerifierFactory {
    private VerifierFactory() {
        throw new IllegalStateException("Factory class");
    }

    private static final String JSON_PROCESSING_FAILED = "JSON processing failed";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JWSVerifier getVerifier(DidDocumentDto crt, URI keyId) throws SIOPAuthenticationException {
        if (keyId == null) {
            throw new SIOPAuthenticationException("Unable to find verifier from DidDocument");
        }
        try {
            VerificationMethodDto foundVerificationMethod = crt.getVerificationMethod().stream()
                    .filter(v -> v.getId().equals(keyId.toString()))
                    .findFirst()
                    .orElseThrow(() -> new SIOPAuthenticationException("Key not found: " + keyId));

            JWK jwk = JWK.parse(objectMapper.writeValueAsString(foundVerificationMethod.getPublicKeyJwk()));
            return JWSVerifierFactory.createVerifier(jwk, new HashSet<>(List.of("b64")));
        } catch (ParseException | JOSEException | JsonProcessingException ex) {
            throw new SIOPAuthenticationException(JSON_PROCESSING_FAILED, ex);
        }
    }
}
