package fi.dvv.digiid.ho.vdr.config;

import fi.dvv.digiid.ho.common.spring.DigiIdConfiguration;
import fi.dvv.digiid.ho.vdr.spring.VdrConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = DigiIdConfiguration.CONFIG_PREFIX_VDR)
public class VdrConfigurationConfig extends VdrConfiguration {

}
