package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class OIDCConfig {

    private String issuer;
    private String authorization_endpoint;
    private String token_endpoint;
    private String jwks_uri;
    private List<String> scopes_supported = List.of("openid");
    private List<String> response_types_supported = List.of("code");
    private List<String> grant_types_supported = List.of("authorization_code");
    private List<String> subject_types_supported = List.of("pairwise");
    private List<String> id_token_signing_alg_values_supported = List.of("RS256");
    private List<String> id_token_encryption_alg_values_supported = List.of("RSA-OAEP");
    private List<String> id_token_encryption_enc_values_supported = List.of("A256GCM");
    private List<String> token_endpoint_auth_methods_supported = List.of("private_key_jwt");
    private List<String> token_endpoint_auth_signing_alg_values_supported = List.of("RS256");
    private String service_documentation = "none";
}
