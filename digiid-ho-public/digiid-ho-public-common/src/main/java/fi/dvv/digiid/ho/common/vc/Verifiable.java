package fi.dvv.digiid.ho.common.vc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Verifiable<T extends Proof>  {

    @JsonProperty("@context")
    private List<URI> context = new ArrayList<>();

    public abstract T getProof();
    public abstract void setProof(T proof);
}
