package fi.dvv.digiid.ho.op.restservice.rest.oidc.validator;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReferenceImpl;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReferenceTest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ACRValidatorProvider {

    @Bean
    @ConditionalOnProperty(name = "fi.dvv.digiid.op.useTestAcrValues", havingValue = "false")
    public ACRValidator defaultAcrValidator() {
        return authRequest -> {
            if (authRequest.getAcrValues().get(0) instanceof AuthenticationContextClassReferenceTest) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot use test LoAs in non-test environments");
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "fi.dvv.digiid.op.useTestAcrValues", havingValue = "true")
    public ACRValidator testAcrValidator() {
        return authRequest -> {
            if (authRequest.getAcrValues().get(0) instanceof AuthenticationContextClassReferenceImpl) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot use non-test LoAs in test environments");
            }
        };
    }
}
