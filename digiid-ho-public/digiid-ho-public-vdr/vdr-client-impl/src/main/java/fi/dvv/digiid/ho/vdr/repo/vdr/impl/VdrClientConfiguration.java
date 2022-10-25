package fi.dvv.digiid.ho.vdr.repo.vdr.impl;

import fi.dvv.digiid.ho.common.spring.DigiIdConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = DigiIdConfiguration.CONFIG_PREFIX_VDR_CLIENT)
@Data
public class VdrClientConfiguration {
    public static final String VDR_CLIENT = "fi.dvv.digiid.vdr.webclient";
    private int timeoutSeconds = 60;
    private String allowedUrl;
}
