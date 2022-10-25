package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPPresentationSubmission {

    @JsonProperty("id")
    private String id;

    @JsonProperty("definition_id")
    private String definitionId;

    @JsonProperty("descriptor_map")
    private List<SIOPDescriptor> descriptorMap = new ArrayList<>();
}
