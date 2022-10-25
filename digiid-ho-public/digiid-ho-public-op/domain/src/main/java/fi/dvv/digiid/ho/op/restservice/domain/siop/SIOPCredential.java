package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SIOPCredential {

    private boolean validationStatus;
    private String credential;
    private Object value;
}
