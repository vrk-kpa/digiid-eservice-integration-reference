package fi.dvv.digiid.ho.op.restservice.service.oidc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OIDCJwtPublicKeyRepository extends CrudRepository<OIDCJwkPublicKey, String>, QueryByExampleExecutor<OIDCJwkPublicKey> {}
