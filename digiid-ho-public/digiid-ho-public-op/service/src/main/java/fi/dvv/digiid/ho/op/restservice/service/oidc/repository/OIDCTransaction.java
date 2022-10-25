package fi.dvv.digiid.ho.op.restservice.service.oidc.repository;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReference;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCTransactionStatus;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPCredential;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@RedisHash(value = "OIDCTransaction", timeToLive = 300)
public class OIDCTransaction implements Serializable {

    @Id
    private String code;
    private long timestamp;
    private OIDCTransactionStatus status;
    private OIDCConfiguration.OidcClient client;
    private String redirectUri;
    private String nonce;
    private String state;
    private List<AuthenticationContextClassReference> acr;
    private List<SIOPCredential> credentials;
}
