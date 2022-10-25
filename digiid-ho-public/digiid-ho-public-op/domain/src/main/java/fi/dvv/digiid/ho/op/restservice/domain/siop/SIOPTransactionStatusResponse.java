package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPTransactionStatusResponse {

    private SIOPTransactionStatus status;
    private String error;
    private List<SIOPCredential> credentials;
}
