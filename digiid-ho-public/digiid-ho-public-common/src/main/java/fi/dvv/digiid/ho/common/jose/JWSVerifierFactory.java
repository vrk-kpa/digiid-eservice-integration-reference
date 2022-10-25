package fi.dvv.digiid.ho.common.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;

import java.util.Set;

public class JWSVerifierFactory {

    private JWSVerifierFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static JWSVerifier createVerifier(JWK jwk) throws JOSEException {
        return createVerifier(jwk, null);
    }

    public static JWSVerifier createVerifier(JWK jwk, Set<String> ecCritHeaders) throws JOSEException {
        if (jwk.getKeyType() == KeyType.EC) {
            return new ECDSAVerifier(jwk.toECKey().toECPublicKey(), ecCritHeaders);
        } else {
            return new RSASSAVerifier(jwk.toRSAKey().toRSAPublicKey());
        }
    }
}
