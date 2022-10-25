package fi.dvv.digiid.ho.common.jsonld;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.util.JWSUtil;
import info.weboftrust.ldsignatures.util.SHAUtil;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class JsonLdUtil {
    private JsonLdUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] canonicalize(LdProof ldProof, JsonLDObject jsonLdObject) throws IOException, GeneralSecurityException, JsonLDException {
        LdProof ldProofWithoutProofValues = LdProof.builder()
                .base(ldProof)
                .contexts(getContexts())
                .defaultContexts(false)
                .build();
        LdProof.removeLdProofValues(ldProofWithoutProofValues);
        JsonLDObject jsonLdObjectWithoutProof = JsonLDObject.builder().base(jsonLdObject).build();
        jsonLdObjectWithoutProof.setDocumentLoader(jsonLdObject.getDocumentLoader());
        LdProof.removeFromJsonLdObject(jsonLdObjectWithoutProof);
        String canonicalizedLdProofWithoutProofValues = ldProofWithoutProofValues.normalize("urdna2015");
        String canonicalizedJsonLdObjectWithoutProof = jsonLdObjectWithoutProof.normalize("urdna2015");
        byte[] canonicalizationResult = new byte[64];
        System.arraycopy(SHAUtil.sha256(canonicalizedLdProofWithoutProofValues), 0, canonicalizationResult, 0, 32);
        System.arraycopy(SHAUtil.sha256(canonicalizedJsonLdObjectWithoutProof), 0, canonicalizationResult, 32, 32);

        return canonicalizationResult;
    }

    public static boolean verifySignatureJwk(byte[] input, String jws, JWSVerifier verifier) throws JOSEException, ParseException {
        if (jws == null) {
            return false;
        }
        JWSObject detachedJwsObject = JWSObject.parse(jws);
        byte[] jwsSigningInput = JWSUtil.getJwsSigningInput(detachedJwsObject.getHeader(), input);
        return verifier.verify(detachedJwsObject.getHeader(), jwsSigningInput, detachedJwsObject.getSignature());
    }

    public static List<URI> getContexts() {
        List<URI> contexts = new ArrayList<>();
        contexts.add(URI.create("https://w3id.org/security/v2"));
        contexts.add(URI.create("https://w3id.org/security/suites/jws-2020/v1"));
        return contexts;
    }
}
