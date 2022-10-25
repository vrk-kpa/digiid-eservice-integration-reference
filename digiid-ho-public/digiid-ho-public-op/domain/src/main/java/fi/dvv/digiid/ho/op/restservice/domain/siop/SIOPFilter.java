package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SIOPFilter {

    @JsonProperty("type")
    private String type;

    @JsonProperty("const")
    private List<String> constraints = new ArrayList<>();
}