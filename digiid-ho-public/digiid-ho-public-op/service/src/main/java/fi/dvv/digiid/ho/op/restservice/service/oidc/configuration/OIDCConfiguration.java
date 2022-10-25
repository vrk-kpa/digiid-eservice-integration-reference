package fi.dvv.digiid.ho.op.restservice.service.oidc.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class OIDCConfiguration {
    @Data
    public static class OidcClient {
        private String clientId;
        private String redirectUri;
        private String publicKey;
    }

    private List<OidcClient> oidcClientList;
}
