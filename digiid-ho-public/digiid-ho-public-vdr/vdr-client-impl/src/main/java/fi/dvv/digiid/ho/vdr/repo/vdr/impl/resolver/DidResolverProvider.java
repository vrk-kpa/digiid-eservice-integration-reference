package fi.dvv.digiid.ho.vdr.repo.vdr.impl.resolver;

import fi.dvv.digiid.ho.vdr.exceptions.InvalidDidException;
import fi.dvv.digiid.ho.vdr.repo.vdr.impl.VdrClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static fi.dvv.digiid.ho.vdr.repo.vdr.impl.resolver.DidResolver.*;

@Component
public class DidResolverProvider {

    private final VdrClientConfiguration configuration;

    public DidResolverProvider(VdrClientConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public DidResolver didResolver() {
        return didWeb -> {
            try {
                List<String> didWebParts = Arrays.asList(validateAndStripDidWeb(didWeb).split(":"));
                StringBuilder didUrlBuilder = new StringBuilder();
                URL allowedUrl = new URL(configuration.getAllowedUrl());
                didUrlBuilder.append(allowedUrl.getProtocol()).append("://");
                didUrlBuilder.append(didWebParts.size() == 1
                        // If no path has been specified in the URL, append /.well-known.
                        ? getWellKnownDidPath(decodeDidWebPart(didWebParts.get(0)))
                        : getDidPath(didWebParts));
                String didUrl = didUrlBuilder.toString();
                if (!didUrl.startsWith(configuration.getAllowedUrl())) {
                    throw new InvalidDidException("Not allowed did url " + didUrl);
                }
                return didUrl;
            } catch (MalformedURLException e) {
                throw new InvalidDidException();
            }
        };
    }
}
