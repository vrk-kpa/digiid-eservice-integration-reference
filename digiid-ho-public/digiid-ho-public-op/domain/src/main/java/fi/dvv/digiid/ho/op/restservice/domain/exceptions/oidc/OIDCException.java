package fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc;


public class OIDCException extends Exception {

    public OIDCException(String msg) {
        super(msg);
    }

    public OIDCException(Exception e) {
        super(e);
    }
}
