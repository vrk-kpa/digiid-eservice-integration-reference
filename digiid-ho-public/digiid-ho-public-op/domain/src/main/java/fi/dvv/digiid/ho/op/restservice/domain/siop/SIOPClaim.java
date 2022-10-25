package fi.dvv.digiid.ho.op.restservice.domain.siop;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SIOPClaim {
    AGE_OVER_15("age_over_15"),
    AGE_OVER_18("age_over_18"),
    AGE_OVER_20("age_over_20"),
    BIRTH_DATE("birth_date"),
    FAMILY_NAME("family_name"),
    GIVEN_NAME("given_name"),
    NATIONALITY("nationality"),
    PERSONAL_IDENTITY_CODE("personal_identity_code"),
    SEX("sex"),
    IDENTIFICATION_METHOD("identification_method"),
    DOCUMENT_ID("document_id"),
    LEVEL_OF_ASSURANCE("level_of_assurance");

    private String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
