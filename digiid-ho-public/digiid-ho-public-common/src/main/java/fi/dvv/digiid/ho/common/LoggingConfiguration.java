package fi.dvv.digiid.ho.common;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class LoggingConfiguration {

    @Bean
    public ErrorAttributes sparseErrorAttributes() {
        return new DefaultErrorAttributes();
    }

    @PostConstruct
    void postConstruct() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
