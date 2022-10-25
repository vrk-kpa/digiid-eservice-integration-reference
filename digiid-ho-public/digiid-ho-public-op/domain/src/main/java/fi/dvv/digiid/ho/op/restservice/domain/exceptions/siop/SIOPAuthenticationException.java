package fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop;

public class SIOPAuthenticationException extends SIOPException {

    public SIOPAuthenticationException(String msg) {
        super(msg);
    }

    public SIOPAuthenticationException(Exception e) {
        super(e);
    }

    public SIOPAuthenticationException(String msg, Exception e) {
        super(msg, e);
    }
}
