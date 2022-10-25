package fi.dvv.digiid.ho.common.x509;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CertErrorException extends Exception {

    public CertErrorException(String message) {
        super(message);
    }

    public CertErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
