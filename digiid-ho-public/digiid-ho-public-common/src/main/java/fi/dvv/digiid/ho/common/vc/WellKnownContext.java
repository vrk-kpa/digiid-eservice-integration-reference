package fi.dvv.digiid.ho.common.vc;

import lombok.Data;

@Data
public final class WellKnownContext {
    private WellKnownContext() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CREDENTIALS_V1 = "https://www.w3.org/2018/credentials/v1";
    public static final String CREDENTIALS_JWS_V1 = "https://w3id.org/security/suites/jws-2020/v1";
    public static final String DID_PREFIX_VTJ = "did:vtj:";
}
