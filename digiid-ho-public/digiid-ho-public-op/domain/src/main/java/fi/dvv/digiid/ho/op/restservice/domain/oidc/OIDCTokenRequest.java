package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.validation.MustEqual;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@ToString
public class OIDCTokenRequest {

    @MustEqual("authorization_code")
    @Getter
    private String grantType;

    @NotBlank
    @Getter
    @Setter
    private String code;

    @Getter
    private String clientId;

    @NotBlank
    @Getter
    private String redirectUri;

    @MustEqual("urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
    @Getter
    private String clientAssertionType;

    @NotBlank
    @Getter
    private String clientAssertion;

    public void setGrant_type (String grantType) {
        this.grantType = grantType;
    }

    public void setClient_id(String clientId) {
        this.clientId = clientId != null ? URLDecoder.decode(clientId, StandardCharsets.UTF_8) : null;
    }

    public void setRedirect_uri(String redirectUri) {
        this.redirectUri = redirectUri != null ? URLDecoder.decode(redirectUri, StandardCharsets.UTF_8) : null;
    }

    public void setClient_assertion_type(String clientAssertionType) {
        this.clientAssertionType = clientAssertionType != null ? URLDecoder.decode(clientAssertionType, StandardCharsets.UTF_8) : null;
    }

    public void setClient_assertion(String clientAssertion) {
        this.clientAssertion = clientAssertion;
    }
}
