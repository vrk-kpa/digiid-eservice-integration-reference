package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPVpToken {

    @JsonProperty("presentation_submission")
    private SIOPPresentationSubmission presentationSubmission;
}
