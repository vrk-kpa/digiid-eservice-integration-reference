package fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc;

public class OIDCAuthenticationException extends OIDCException {

    public OIDCAuthenticationException(Exception e) {
        super(e);
    }
}
