package fi.dvv.digiid.ho.common.vc.did;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DidDocumentDto {
    @JsonIgnore
    public static final String DID_V1 = "https://www.w3.org/ns/did/v1";
    @JsonIgnore
    public static final String W3ID_V1 = "https://w3id.org/security/suites/jws-2020/v1";

    public DidDocumentDto() {
        this.getContext().add(URI.create(DID_V1));
        this.getContext().add(URI.create(W3ID_V1));
    }

    @JsonProperty("@context")
    private List<URI> context = new ArrayList<>();

    @JsonProperty("id")
    private String id;

    @JsonProperty("verificationMethod")
    private List<VerificationMethodDto> verificationMethod = new ArrayList<>();

    @JsonProperty("authentication")
    private List<String> authentication = new ArrayList<>();

    @JsonProperty("assertionMethod")
    private List<String> assertionMethod = new ArrayList<>();

    @JsonProperty("controller")
    private List<String> controller = new ArrayList<>();
}