package fi.dvv.digiid.ho.common.x509;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Slf4j
public class CertificateLoader {

    private static final Pattern CERT_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+" +
                    "([a-z0-9+/=\\r\\n]+)" +
                    "-+END\\s+.*CERTIFICATE[^-]*-+",
            CASE_INSENSITIVE);
    private static CertificateFactory certificateFactory;

    static {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public static X509Certificate loadCertificate(String pem) throws CertErrorException {

        try {
            Matcher matcher = CERT_PATTERN.matcher(safeDecode(pem));
            List<X509Certificate> certificates = new ArrayList<>();

            int start = 0;
            if (matcher.find(start)) {
                byte[] buffer = Base64.decode(matcher.group(1));
                return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(buffer));
            }

            throw new CertErrorException("Certificate parsing failed");
        } catch (CertificateException e) {
            throw new CertErrorException("Can not read certificate from request", e);
        }
    }

    private static CharSequence safeDecode(String pem) {
        if (pem.indexOf((char) 10) > -1 || pem.indexOf((char) 13) > -1) {
            return pem;
        }
        return URLDecoder.decode(pem, StandardCharsets.UTF_8);

    }

}
