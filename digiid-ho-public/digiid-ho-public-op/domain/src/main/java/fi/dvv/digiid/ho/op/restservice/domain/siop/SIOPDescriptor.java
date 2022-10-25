package fi.dvv.digiid.ho.op.restservice.domain.siop;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class SIOPDescriptor {

    private String id;
    private String format;
    private String path;
}
