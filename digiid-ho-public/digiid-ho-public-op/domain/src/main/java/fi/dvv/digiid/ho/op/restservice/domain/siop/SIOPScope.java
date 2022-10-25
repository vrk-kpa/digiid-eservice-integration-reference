package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SIOPScope {

    DIGIID_CORE("digiid_core"),
    OPENID("openid");

    private String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
