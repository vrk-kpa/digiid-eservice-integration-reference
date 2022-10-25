package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Builder
@ToString
@AllArgsConstructor(onConstructor_ = @ConstructorProperties({"id_token", "vp_token", "error", "state"}))
public class SIOPAuthRequest {

    @Getter
    private String idToken;

    @Getter
    private String vpToken;

    @Getter
    private String error;

    @Getter
    private String state;
}
