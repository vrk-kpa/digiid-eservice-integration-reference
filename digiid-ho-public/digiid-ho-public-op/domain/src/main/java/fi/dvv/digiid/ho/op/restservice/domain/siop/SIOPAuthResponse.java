package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPAuthResponse {

    @NotBlank
    private String status;
}
