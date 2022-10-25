package fi.dvv.digiid.ho.op.restservice.service.oidc.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Data
@AllArgsConstructor
@RedisHash(value = "OIDCJwkPublicKey", timeToLive = 1200)
public class OIDCJwkPublicKey {

    @Id
    private String url;

    private String keys;
}
