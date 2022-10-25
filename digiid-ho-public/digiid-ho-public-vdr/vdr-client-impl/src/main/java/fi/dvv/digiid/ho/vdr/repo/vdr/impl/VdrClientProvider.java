package fi.dvv.digiid.ho.vdr.repo.vdr.impl;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VdrClientProvider {

    @Bean
    @Qualifier(VdrClientConfiguration.VDR_CLIENT)
    public WebClient getVdrClient() {
        // VDRClient requires SSL Context since it is accessed through its public DNS name and therefore uses HTTPS
        // The public DNS name is used because of the VerifiablePresentation specification
        // namely its DID URL requirements
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        JettyClientHttpConnector clientConnector = new JettyClientHttpConnector(new HttpClient(sslContextFactory));
        return WebClient.builder()
                .clientConnector(clientConnector)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(20 * 1024 * 1024))
                        .build()).build();
    }
}
