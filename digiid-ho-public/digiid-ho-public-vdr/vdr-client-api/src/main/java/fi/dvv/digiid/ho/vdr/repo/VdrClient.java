package fi.dvv.digiid.ho.vdr.repo;


import fi.dvv.digiid.ho.common.vc.did.DidDocumentDto;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface VdrClient {

    Mono<DidDocumentDto> getCertByDid(@PathVariable("entityid") String did);
}
