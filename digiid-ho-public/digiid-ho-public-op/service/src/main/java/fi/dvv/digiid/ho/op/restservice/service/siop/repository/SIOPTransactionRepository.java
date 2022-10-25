package fi.dvv.digiid.ho.op.restservice.service.siop.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SIOPTransactionRepository extends CrudRepository<SIOPTransaction, String>, QueryByExampleExecutor<SIOPTransaction> {}
