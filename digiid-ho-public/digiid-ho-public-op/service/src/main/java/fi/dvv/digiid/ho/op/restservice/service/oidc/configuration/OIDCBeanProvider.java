package fi.dvv.digiid.ho.op.restservice.service.oidc.configuration;

import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReferenceImpl;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReferenceMapper;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.AuthenticationContextClassReferenceTest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OIDCBeanProvider {
    @Bean
    @ConditionalOnProperty(name = "fi.dvv.digiid.op.useTestAcrValues", havingValue = "true")
    public AuthenticationContextClassReferenceMapper getAuthenticationContextClassReferenceTestMapper() {
        return loAValue -> {
            List<String> credentialValue = List.of(loAValue.split(" "));
            return credentialValue.stream()
                    .map(AuthenticationContextClassReferenceTest::fromLoA)
                    .toList();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "fi.dvv.digiid.op.useTestAcrValues", havingValue = "false")
    public AuthenticationContextClassReferenceMapper getAuthenticationContextClassReferenceImplMapper() {
        return loAValue -> {
            List<String> credentialValue = List.of(loAValue.split(" "));
            return credentialValue.stream()
                    .map(AuthenticationContextClassReferenceImpl::fromLoA)
                    .toList();
        };
    }
}
