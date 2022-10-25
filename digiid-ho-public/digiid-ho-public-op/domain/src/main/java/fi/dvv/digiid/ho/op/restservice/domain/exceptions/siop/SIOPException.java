package fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop;

public class SIOPException extends Exception {

    public SIOPException(String msg) {
        super(msg);
    }

    public SIOPException(Exception e) {
        super(e);
    }

    public SIOPException(String msg, Exception e) {
        super(msg, e);
    }
}
