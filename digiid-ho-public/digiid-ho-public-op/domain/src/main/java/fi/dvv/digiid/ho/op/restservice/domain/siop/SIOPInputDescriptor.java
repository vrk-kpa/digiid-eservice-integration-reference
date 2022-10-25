package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SIOPInputDescriptor {

    @JsonProperty("id")
    private String id;

    @JsonProperty("constraints")
    private SIOPConstraints constraints;
}