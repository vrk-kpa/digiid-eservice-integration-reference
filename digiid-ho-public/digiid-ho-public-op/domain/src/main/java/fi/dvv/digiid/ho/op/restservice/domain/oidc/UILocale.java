package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UILocale {
    FI("fi"),
    SV("sv"),
    EN("en");

    private String code;

    public String getCode() {
        return code;
    }

    public static UILocale fromCode(String code) {
        for (UILocale locale : values()) {
            if (locale.getCode().equals(code)) {
                return locale;
            }
        }
        return null;
    }
}
