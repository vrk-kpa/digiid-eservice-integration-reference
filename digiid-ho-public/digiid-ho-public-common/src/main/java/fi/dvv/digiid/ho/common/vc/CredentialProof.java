package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@SuperBuilder
@Data
@JsonPropertyOrder({"type", "created", "proofPurpose", "verificationMethod", "jws"})
public class CredentialProof extends Proof {
}
