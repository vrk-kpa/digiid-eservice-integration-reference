package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"@context", "type", "verifiableCredential", "holder", "proof"})
public class VerifiablePresentation extends Verifiable<PresentationProof> {

    @JsonProperty("type")
    private List<String> type = List.of("VerifiablePresentation");

    @JsonProperty("verifiableCredential")
    private List<VerifiableCredential> verifiableCredentials = new ArrayList<>();

    public VerifiablePresentation() {
        this.getContext().add(URI.create(WellKnownContext.CREDENTIALS_V1));
        this.getContext().add(URI.create(WellKnownContext.CREDENTIALS_JWS_V1));
    }

    /**
     * Convenience method that makes sure contexts are updated properly
     *
     * @param credential the VerifiableCredential to add
     */
    public void addVerifiableCredential(VerifiableCredential credential) {
        credential.getContext()
                .stream()
                .filter(c -> !this.getContext().contains(c))
                .forEach(c -> this.getContext().add(c));
        if (!this.getVerifiableCredentials().contains(credential)) {
            this.getVerifiableCredentials().add(credential);
        }
    }

    @JsonProperty("holder")
    private String holder;

    @JsonProperty("proof")
    private PresentationProof proof;

}
