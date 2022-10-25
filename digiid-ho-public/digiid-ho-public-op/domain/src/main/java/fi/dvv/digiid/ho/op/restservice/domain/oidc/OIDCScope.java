package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OIDCScope {
    OPENID("openid"),
    PERSONAL_IDENTITY_CODE("personal_identity_code");

    private String value;

    public String getValue() {
        return value;
    }

    public static OIDCScope fromValue(String value) {
        for (OIDCScope scope : values()) {
            if (scope.getValue().equals(value)) {
                return scope;
            }
        }
        return null;
    }
}
