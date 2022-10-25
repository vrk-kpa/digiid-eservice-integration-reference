package fi.dvv.digiid.ho.vdr.repo.vdr.impl;

import fi.dvv.digiid.ho.common.vc.did.DidDocumentDto;
import fi.dvv.digiid.ho.vdr.exceptions.DidDocumentNotFoundException;
import fi.dvv.digiid.ho.vdr.exceptions.InvalidDidException;
import fi.dvv.digiid.ho.vdr.repo.VdrClient;
import fi.dvv.digiid.ho.vdr.repo.vdr.impl.resolver.DidResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class VdrClientImpl implements VdrClient {

    private final VdrClientConfiguration configuration;
    private final WebClient vdrClient;
    private final DidResolver didResolver;

    public VdrClientImpl(VdrClientConfiguration configuration,
                         @Qualifier(VdrClientConfiguration.VDR_CLIENT) WebClient vdrClient,
                         DidResolver didResolver) {
        this.configuration = configuration;
        this.vdrClient = vdrClient;
        this.didResolver = didResolver;
    }

    @Override
    public Mono<DidDocumentDto> getCertByDid(String did) {
        try {
            return this.vdrClient
                    .get()
                    .uri(didResolver.resolve(did))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(DidDocumentDto.class)
                    .switchIfEmpty(Mono.error(new DidDocumentNotFoundException()))
                    .timeout(Duration.ofSeconds(configuration.getTimeoutSeconds()));
        } catch (InvalidDidException e) {
            return Mono.error(e);
        }
    }
}
