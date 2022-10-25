package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@SuperBuilder
@Data
@JsonPropertyOrder({"type", "created", "proofPurpose", "verificationMethod", "challenge", "domain", "jws"})
public class PresentationProof extends Proof {

    @JsonProperty("challenge")
    private String challenge;

    @JsonProperty("domain")
    private String domain;
}
