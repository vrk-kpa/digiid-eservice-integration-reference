package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPIdToken {

    @NotBlank
    private String iss;

    @NotBlank
    private String aud;

    @PastOrPresent
    private Date iat;

    @FutureOrPresent
    private Date exp;

    @NotBlank
    private String sub;

    @PastOrPresent
    private Date auth_time;

    @NotBlank
    private String nonce;

    @NotNull
    @Valid
    private SIOPVpToken _vp_token;
}
