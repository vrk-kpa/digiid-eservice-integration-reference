package fi.dvv.digiid.ho.common.jsonld;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class SpringDocumentLoader implements DocumentLoader {
    private final JsonDocument w3Document;
    private final JsonDocument digiidSchema;
    private final JsonDocument w3SecurityJWS2020V1Schema;

    public SpringDocumentLoader() throws IOException, JsonLdError {
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        Resource w3Resource = resourceLoader.getResource("classpath:w3-2018-credentials-v1.json");
        Reader reader = new InputStreamReader(w3Resource.getInputStream(), UTF_8);
        this.w3Document = JsonDocument.of(reader);
        reader.close();

        Resource digiidResource = resourceLoader.getResource("classpath:digiid-core-ld-schema-1.0.json");
        reader = new InputStreamReader(digiidResource.getInputStream(), UTF_8);
        this.digiidSchema = JsonDocument.of(reader);
        reader.close();

        Resource w3SecurityJWS2020Resource = resourceLoader.getResource("classpath:w3-security-jws-2020-v1.json");
        reader = new InputStreamReader(w3SecurityJWS2020Resource.getInputStream(), UTF_8);
        this.w3SecurityJWS2020V1Schema = JsonDocument.of(reader);
        reader.close();
    }

    @Override
    public Document loadDocument(URI uri, DocumentLoaderOptions documentLoaderOptions) {
        if (uri.toString().contains("https://www.w3.org/2018/credentials/v1")) {
            return w3Document;
        }
        if (uri.toString().contains("schemas/digiid-core-schema-1.0.json")) {
            return digiidSchema;
        }
        if (uri.toString().contains("https://w3id.org/security/suites/jws-2020/v1")) {
            return w3SecurityJWS2020V1Schema;
        }
        return null;
    }
}