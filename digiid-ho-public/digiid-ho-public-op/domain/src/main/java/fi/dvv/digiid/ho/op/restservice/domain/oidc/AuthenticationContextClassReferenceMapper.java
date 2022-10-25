package fi.dvv.digiid.ho.op.restservice.domain.oidc;

import java.util.List;

public interface AuthenticationContextClassReferenceMapper {
    List<AuthenticationContextClassReference> mapLoAToACR(String loAValue);
}
