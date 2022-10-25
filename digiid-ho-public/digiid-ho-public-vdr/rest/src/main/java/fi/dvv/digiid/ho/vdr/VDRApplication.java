package fi.dvv.digiid.ho.vdr;

import fi.dvv.digiid.ho.common.GlobalExceptionHandler;
import fi.dvv.digiid.ho.common.LoggingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackageClasses = {VDRApplication.class})
@Import({LoggingConfiguration.class, GlobalExceptionHandler.class})
public class VDRApplication {
        public static void main(String[] args) {
                SpringApplication.run(VDRApplication.class, args);
        }

}

