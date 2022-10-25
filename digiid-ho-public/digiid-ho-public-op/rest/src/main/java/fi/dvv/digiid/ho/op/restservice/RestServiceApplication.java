package fi.dvv.digiid.ho.op.restservice;
  
import fi.dvv.digiid.ho.common.GlobalExceptionHandler;
import fi.dvv.digiid.ho.common.LoggingConfiguration;
import fi.dvv.digiid.ho.common.jsonld.SpringDocumentLoader;
import fi.dvv.digiid.ho.vdr.repo.vdr.impl.VdrClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Slf4j
@SpringBootApplication(scanBasePackageClasses = {RestServiceApplication.class, VdrClientImpl.class, SpringDocumentLoader.class})
@Import({LoggingConfiguration.class, GlobalExceptionHandler.class})
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();
        map.forEach((key, value) -> log.info("{} {}", key, value));
    }
}
