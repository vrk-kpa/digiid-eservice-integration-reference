package fi.dvv.digiid.ho.op.restservice.service.siop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPTransactionStatus;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransactionRepository;

public abstract class SIOPServiceBase {

    protected final SIOPTransactionRepository transactionRepository;

    protected final ObjectMapper objectMapper;

    protected SIOPServiceBase(SIOPTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = new ObjectMapper();
    }

    protected SIOPTransaction findCreatedTransaction(String nonce) throws SIOPTransactionNotFoundException, SIOPAuthenticationFailedException, SIOPAuthenticationBadRequestException {
        if (nonce == null) {
            throw new SIOPAuthenticationBadRequestException("Unable to find SIOP transaction, nonce cannot be null");
        }

        SIOPTransaction transaction = transactionRepository.findById(nonce)
                .orElseThrow(SIOPTransactionNotFoundException::new);

        if (transaction.getStatus() != SIOPTransactionStatus.CREATED) {
            throw new SIOPAuthenticationFailedException("Authentication failed: Transaction not in created state");
        }
        return transaction;
    }

}
