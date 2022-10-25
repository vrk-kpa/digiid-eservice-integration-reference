package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class OIDCTransactionStatusResponse {

    private OIDCTransactionStatus status;
    private String redirect;
}
