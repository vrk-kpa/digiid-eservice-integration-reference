package fi.dvv.digiid.ho.op.restservice.domain.siop;

import fi.dvv.digiid.ho.op.restservice.domain.siop.validation.ValidSIOPClaims;
import fi.dvv.digiid.ho.op.restservice.domain.siop.validation.ValidSIOPScopes;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPLoginRequest {

    @ValidSIOPScopes(SIOPScope.DIGIID_CORE)
    private List<SIOPScope> scopes;

    @ValidSIOPClaims({SIOPClaim.AGE_OVER_15, SIOPClaim.AGE_OVER_18, SIOPClaim.AGE_OVER_20,
            SIOPClaim.BIRTH_DATE, SIOPClaim.FAMILY_NAME, SIOPClaim.GIVEN_NAME,
            SIOPClaim.NATIONALITY, SIOPClaim.PERSONAL_IDENTITY_CODE,
            SIOPClaim.SEX, SIOPClaim.IDENTIFICATION_METHOD, SIOPClaim.DOCUMENT_ID, SIOPClaim.LEVEL_OF_ASSURANCE})
    private List<SIOPClaim> claims;

    private String presentationDefinition;

    @NotBlank
    @Length(min = 32)
    private String nonce;
}
