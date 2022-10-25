package fi.dvv.digiid.ho.op.restservice.service.siop.service;

import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPTransactionStatus;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SIOPTransactionService extends SIOPServiceBase {


    @Autowired
    public SIOPTransactionService(SIOPTransactionRepository transactionRepository) {
        super(transactionRepository);
    }

    public SIOPTransaction getTransaction(String nonce) throws SIOPTransactionNotFoundException {
        return transactionRepository.findById(nonce).orElseThrow(SIOPTransactionNotFoundException::new);
    }

    public SIOPTransaction getTransactionAndDeleteIfReady(String nonce) throws SIOPTransactionNotFoundException {
        SIOPTransaction transaction = getTransaction(nonce);
        if (transaction.getStatus() == SIOPTransactionStatus.READY) {
            transactionRepository.deleteById(nonce);
        }

        return transaction;
    }

    public SIOPTransaction getTransactionAndDelete(String nonce) throws SIOPTransactionNotFoundException {
        SIOPTransaction transaction = getTransaction(nonce);
        transactionRepository.deleteById(nonce);

        return transaction;
    }
}
