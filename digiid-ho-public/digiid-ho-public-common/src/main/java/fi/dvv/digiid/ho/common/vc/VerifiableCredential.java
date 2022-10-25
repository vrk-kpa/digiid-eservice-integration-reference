package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.net.URI;
import java.util.*;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@context",
        "id",
        "type",
        "issuer",
        "issuanceDate",
        "credentialSubject",
        "credentialStatus",
        "expirationDate",
        "proof"})
public class VerifiableCredential extends Verifiable<CredentialProof> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private List<String> type = new ArrayList<>();

    @JsonProperty("issuer")
    private Issuer issuer;

    @JsonProperty("issuanceDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date issuanceDate;

    @JsonProperty("expirationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date expirationDate;

    @JsonProperty("credentialSubject")
    private Map<String, Object> credentialSubject = new LinkedHashMap<>();

    @JsonProperty("credentialStatus")
    private CredentialStatus credentialStatus;

    @JsonProperty("proof")
    private CredentialProof proof;

    public VerifiableCredential() {
        this.type.add("VerifiableCredential");
        this.type.add("DvvCoreId");
        this.getContext().add(URI.create(WellKnownContext.CREDENTIALS_V1));
        this.getContext().add(URI.create(WellKnownContext.CREDENTIALS_JWS_V1));

    }

    public void addCredential(String id, String credentialName, Object value) {
        credentialSubject.put("id", id);
        credentialSubject.put(credentialName, value);
        type.add(credentialName);
    }

}
