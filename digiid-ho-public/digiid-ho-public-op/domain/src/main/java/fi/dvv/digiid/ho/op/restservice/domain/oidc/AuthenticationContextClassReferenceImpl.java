package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum AuthenticationContextClassReferenceImpl implements AuthenticationContextClassReference {

    FICORA_LOA3("http://ftn.ficora.fi/2017/loa3"),
    FICORA_LOA2("http://ftn.ficora.fi/2017/loa2"),
    URN_OID_8("urn:oid:1.2.246.517.3002.110.8"),
    URN_OID_9("urn:oid:1.2.246.517.3002.110.9"),
    URN_OID_10("urn:oid:1.2.246.517.3002.110.10");

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

        return Stream.of(values())
                .filter(c -> c.uri.equals(uri))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
