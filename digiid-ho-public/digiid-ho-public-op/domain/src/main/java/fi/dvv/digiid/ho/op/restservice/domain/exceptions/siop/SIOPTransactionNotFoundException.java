package fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop;

public class SIOPTransactionNotFoundException extends SIOPException {

    public SIOPTransactionNotFoundException() {
        super("SIOP transaction not found");
    }
}
