package fi.dvv.digiid.ho.vdr.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class InfoProvider {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("DigiId Vdr Microservice")
                        .description("DigiId Vdr Microservice")
                        .version("v0.1")
                );
    }
}
