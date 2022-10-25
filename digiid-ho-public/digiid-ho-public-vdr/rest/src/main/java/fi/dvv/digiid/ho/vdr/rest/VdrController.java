package fi.dvv.digiid.ho.vdr.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class VdrController {

    private final String dvvCoreSchema;

    private final String hytDidDocument;
    private final String pohaDidDocument;

    private final String dvvDidDocument;
    private static final String RESOURCE_NOT_FOUND = "resource_not_found";

    public VdrController() throws IOException {
        this.dvvCoreSchema = loadResourceFromClasspath("digiid-core-ld-schema-1.0.json");
        this.hytDidDocument = loadResourceFromClasspath("hyt-did-document.json");
        this.pohaDidDocument = loadResourceFromClasspath("poha-did-document.json");
        this.dvvDidDocument = loadResourceFromClasspath("dvv-did-document.json");
    }

    private String loadResourceFromClasspath(String location) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        InputStream stream = resourceLoader.getResource("classpath:" + location).getInputStream();
        String value = new String(stream.readAllBytes());
        stream.close();
        return value;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("Decoding error:", e);
        }
        return value;
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/schemas/{schemaId}")
    public Mono<String> getSchema(@PathVariable("schemaId") String schemaId) {
        try {
            if (!schemaId.equals("digiid-core-schema-1.0.json")) {
                log.warn("SchemaId not digiid-core-schema-1.0.json");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND);
            }
            return Mono.just(this.dvvCoreSchema);
        } catch (Exception e) {
            if (!(e instanceof ResponseStatusException)) {
                log.error("Error occurred", e);
            }
            throw e;
        }
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/hyt/{entityid}/did.json")
    public Mono<String> getCertByHyt(@PathVariable("entityid") String entityid) {
        if (decode(entityid).equals("hyt060168-9861#key-6aff5949-7eef-4fac-9eb8-772e391a2021")) {
            return Mono.just(hytDidDocument);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "use hyt060168-9861#key-6aff5949-7eef-4fac-9eb8-772e391a2021 as entityid"));
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/issuer/{entityid}/did.json")
    public Mono<String> getCertByIssuer(@PathVariable("entityid") String entityid) {
        if (decode(entityid).equals("56e8b13b-f8ab-4e7d-9250-a106f24e6cdf#key-92d3651a-0868-4886-b70d-4a35b0493fb1")) {
            return Mono.just(pohaDidDocument);
        }
        if (decode(entityid).equals("7705abd9-3d96-428a-949d-e85b43f75f2f#key-0f1d9721-3921-4841-91ab-5d2c755b24f4")) {
            return Mono.just(dvvDidDocument);
        }

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "use 56e8b13b-f8ab-4e7d-9250-a106f24e6cdf#key-92d3651a-0868-4886-b70d-4a35b0493fb1 " +
                "or 7705abd9-3d96-428a-949d-e85b43f75f2f#key-0f1d9721-3921-4841-91ab-5d2c755b24f4 as entityid"));
    }
}
