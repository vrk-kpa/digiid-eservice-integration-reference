package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URI;

@JsonPropertyOrder({"id", "type"})
public class CredentialStatus {

    @JsonProperty("id")
    private URI id;

    @JsonProperty("type")
    private String type;
}
