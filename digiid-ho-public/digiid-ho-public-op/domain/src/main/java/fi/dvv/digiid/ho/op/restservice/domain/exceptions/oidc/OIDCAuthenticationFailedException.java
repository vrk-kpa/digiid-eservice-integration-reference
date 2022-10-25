package fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc;

public class OIDCAuthenticationFailedException extends OIDCException {

    public OIDCAuthenticationFailedException(String msg) {
        super(msg);
    }
}
