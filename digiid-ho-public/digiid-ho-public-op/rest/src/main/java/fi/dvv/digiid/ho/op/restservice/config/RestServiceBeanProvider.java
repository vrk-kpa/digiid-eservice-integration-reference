package fi.dvv.digiid.ho.op.restservice.config;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import fi.dvv.digiid.ho.common.x509.CertErrorException;
import fi.dvv.digiid.ho.common.x509.CertificateLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class RestServiceBeanProvider {

    private static final String PRIVATE_KEY_FILENAME = "signing-private-key.pem";
    private static final String CERTIFICATE_FILENAME = "signing-certificate.pem";

    @Bean("signingKey")
    public RSAKey signingKey() throws IOException, JOSEException, CertErrorException {
        String privateKey = new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_FILENAME)).readAllBytes());
        String certificate = new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(CERTIFICATE_FILENAME)).readAllBytes());
        return new RSAKey
                .Builder((RSAKey) JWK.parseFromPEMEncodedObjects(privateKey))
                .keyID(CertificateLoader.loadCertificate(certificate).getSerialNumber().toString())
                .build();
    }

    @Bean("signingCertificate")
    public RSAKey signingCertificate() throws IOException, JOSEException, CertErrorException {
        String certificate = new String(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(CERTIFICATE_FILENAME)).readAllBytes());
        return new RSAKey
                .Builder((RSAKey) JWK.parseFromPEMEncodedObjects(certificate))
                .keyID(CertificateLoader.loadCertificate(certificate).getSerialNumber().toString())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(Algorithm.parse("RS256"))
                .build();
    }
}
