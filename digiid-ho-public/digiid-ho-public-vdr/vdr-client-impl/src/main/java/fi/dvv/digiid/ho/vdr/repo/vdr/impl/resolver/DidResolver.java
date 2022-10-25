package fi.dvv.digiid.ho.vdr.repo.vdr.impl.resolver;

import fi.dvv.digiid.ho.vdr.exceptions.InvalidDidException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface DidResolver {

    String DID_WEB = "did:web:";
    String DID_JSON = "/did.json";
    String WELL_KNOWN_DOC_PATH = "/.well-known" + DID_JSON;
    String DID_URL_FRAGMENT_ID = "#";

    String resolve(String didWeb) throws InvalidDidException;

    static String getWellKnownDidPath(String domain) {
        return domain + WELL_KNOWN_DOC_PATH;
    }

    static String getDidPath(List<String> didWebParts) {
        return didWebParts.stream()
                .map(DidResolver::decodeDidWebPart)
                .map(didWebPart -> {
                    if (didWebPart.contains(DID_URL_FRAGMENT_ID)) {
                        return encodeDidWebPart(didWebPart);
                    } else {
                        return didWebPart;
                    }
                }).collect(Collectors.joining("/", "", DID_JSON));
    }

    static String validateAndStripDidWeb(String didWeb) throws InvalidDidException {
        if (didWeb == null || !didWeb.startsWith(DID_WEB)) {
            throw new InvalidDidException();
        }
        return didWeb.replace(DID_WEB, "");
    }

    static String encodeDidWebPart(String didWebPart) {
        try {
            return URLEncoder.encode(didWebPart, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return didWebPart;
    }

    static String decodeDidWebPart(String didWebPart) {
        try {
            return URLDecoder.decode(didWebPart, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return didWebPart;
    }
}
