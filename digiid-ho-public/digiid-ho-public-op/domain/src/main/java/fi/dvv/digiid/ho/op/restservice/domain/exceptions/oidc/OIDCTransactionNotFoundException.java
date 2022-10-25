package fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc;

public class OIDCTransactionNotFoundException extends OIDCException {

    public OIDCTransactionNotFoundException() {
        super("OIDC transaction not found");
    }
}
