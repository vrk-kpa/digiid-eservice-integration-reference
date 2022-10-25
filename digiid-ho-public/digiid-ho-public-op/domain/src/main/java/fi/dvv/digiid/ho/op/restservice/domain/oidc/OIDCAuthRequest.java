package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.validation.MustEqual;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.validation.RequiredOIDCScopes;
import fi.dvv.digiid.ho.op.restservice.domain.validation.NoNullElements;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OIDCAuthRequest {

    @Getter
    @MustEqual("code")
    @JsonProperty("response_type")
    private String responseType;

    @NotBlank
    @Getter
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank
    @Getter
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @RequiredOIDCScopes(OIDCScope.OPENID)
    @NoNullElements
    @Getter
    private List<OIDCScope> scope;

    @NotBlank
    @Length(min = 32)
    @Getter
    @Setter
    private String nonce;

    @NotBlank
    @Getter
    @Setter
    private String state;

    @NotNull
    @NotEmpty
    @NoNullElements
    @Getter
    @JsonProperty("acr_values")
    private List<AuthenticationContextClassReference> acrValues;

    @NoNullElements
    @Getter
    @JsonProperty("ui_locales")
    private List<UILocale> uiLocales;

    @NotBlank
    @Getter
    @Setter
    @JsonProperty("ftn_spname")
    private String ftnSpname;

    @Getter
    @Setter
    private String request;

    @Getter
    @Setter
    private String aud;

    @Getter
    @Setter
    private String iss;

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId != null ? URLDecoder.decode(clientId, StandardCharsets.UTF_8) : null;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri != null ? URLDecoder.decode(redirectUri, StandardCharsets.UTF_8) : null;
    }

    public void setScope(String scopeStr) {
        if (scopeStr != null) {
            String[] values = scopeStr.split(" ");
            this.scope = Arrays.stream(values)
                    .map(OIDCScope::fromValue)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            this.scope = null;
        }
    }

    public void setAcrValues(String acrValuesStr) {
        if (acrValuesStr != null) {
            String[] values = acrValuesStr.split(" ");
            this.acrValues = Arrays.stream(values)
                    .map(encodedUri -> URLDecoder.decode(encodedUri, StandardCharsets.UTF_8))
                    .map(this::toAcrUri)
                    .toList();
        } else {
            this.acrValues = new ArrayList<>();
        }
    }

    public void setUiLocales(String uiLocalesStr) {
        if (uiLocalesStr != null) {
            String[] values = uiLocalesStr.split(" ");
            this.uiLocales = Arrays.stream(values).map(UILocale::fromCode).toList();
        } else {
            this.uiLocales = List.of(UILocale.FI);
        }
    }

    private AuthenticationContextClassReference toAcrUri(String uri) {
        if (uri.contains("test")) {
            return AuthenticationContextClassReferenceTest.fromACRUri(uri);
        }
        return AuthenticationContextClassReferenceImpl.fromACRUri(uri);
    }
}
