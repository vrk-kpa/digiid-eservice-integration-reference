package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class OIDCClientAssertion {

    private String iss;

    private String sub;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> aud;

    private String jti;

    private long exp;

    private long iat;
}
