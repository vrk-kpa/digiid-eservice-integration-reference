package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.Date;

@NoArgsConstructor
@SuperBuilder
@Data
public abstract class Proof {
    @JsonProperty("type")
    private String type;

    @JsonProperty("created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date created;

    @JsonProperty("challenge")
    private String challenge;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("jws")
    private String jws;

    @JsonProperty("proofPurpose")
    private String proofPurpose;

    @JsonProperty("verificationMethod")
    private URI verificationMethod;

}
