package fi.dvv.digiid.ho.op.restservice.service.siop.repository;

import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPClaim;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPCredential;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPTransactionStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@RedisHash(value = "SIOPTransaction", timeToLive = 300)
public class SIOPTransaction implements Serializable {

    @Id
    private String nonce;
    private String clientId;
    private List<SIOPClaim> claims;
    private long timestamp;
    private SIOPTransactionStatus status;
    private String error;
    private List<SIOPCredential> credentials;
    private String presentationDefinition;

}
