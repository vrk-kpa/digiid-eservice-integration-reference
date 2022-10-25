package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum AuthenticationContextClassReferenceTest implements AuthenticationContextClassReference {

    FICORA_LOA3("http://ftn.ficora.fi/2017/loatest3"),
    FICORA_LOA2("http://ftn.ficora.fi/2017/loatest2"),
    URN_OID_8("urn:oid:1.2.246.517.3002.110.8test"),
    URN_OID_9("urn:oid:1.2.246.517.3002.110.9test"),
    URN_OID_10("urn:oid:1.2.246.517.3002.110.10test");

    private final String uri;

    public String getUri() {
        return uri;
    }

    static AuthenticationContextClassReference fromACRUri(String uri) {
        return Stream.of(values())
                .filter(c -> c.uri.equals(uri))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static AuthenticationContextClassReference fromLoA(String uri) {
        // LoAs have no test-postfix for test envs - the acr uri needs to be normalized by not considering the postfix

        return Stream.of(values())
                .filter(c -> c.uri.replace("test", "").equals(uri))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
