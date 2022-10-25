package fi.dvv.digiid.ho.op.restservice.service.siop.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import fi.dvv.digiid.ho.op.restservice.domain.siop.*;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.time.Clock;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
public class SIOPLoginService extends SIOPServiceBase {

    private final Clock clock;
    private final IdGenerator idGenerator;

    @Autowired
    public SIOPLoginService(SIOPTransactionRepository transactionRepository, Clock clock, IdGenerator idGenerator) {
        super(transactionRepository);
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    public String handleLogin(SIOPLoginRequest request, String redirectUri) {
        String authRedirect = UriUtils.encode(redirectUri + "/siop/api/1.0/auth", UTF_8);

        Optional<String> claimsJson = getClaimsJson(request);

        SIOPTransaction transaction = SIOPTransaction.builder()
                .clientId(redirectUri + "/siop/api/1.0/auth")
                .claims(request.getClaims())
                .nonce(request.getNonce())
                .timestamp(clock.millis())
                .status(SIOPTransactionStatus.CREATED)
                .presentationDefinition(claimsJson.orElse(""))
                .build();

        transactionRepository.save(transaction);

        return UriComponentsBuilder.newInstance()
                .scheme("openid")
                .host("")
                .queryParam("scope", (request.getScopes() != null && !request.getScopes().isEmpty() ? getUriEncodedSIOPScope(request) : "openid"))
                .queryParamIfPresent("claims", getURIEncodedClaimsParam(request.getNonce(), redirectUri, claimsJson))
                .queryParam("response_type", "id_token")
                .queryParam("response_mode", "post")
                .queryParam("registration", UriUtils.encode("{\"subject_syntax_types_supported\":\"did:web\",\"id_token_signing_alg_values_supported\":\"ES256\"}", UTF_8))
                .queryParam("client_id", authRedirect)
                .queryParam("redirect_uri", authRedirect)
                .queryParam("nonce", request.getNonce())
                .queryParam("state", request.getNonce())
                .build(true)
                .toUriString();


    }

    private Optional<String> getURIEncodedClaimsParam(String nonce, String redirectUri, Optional<String> claimsJson) {
        if (claimsJson.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of("{\"vp_token\": {" +
                "\"presentation_definition_uri\": \"" + redirectUri + "/siop/api/1.0/presentationdef/" + nonce + "\"}" +
                "}")
                .map(s -> UriUtils.encode(s, UTF_8));
    }

    private Optional<String> getClaimsJson(SIOPLoginRequest request) {
        String presentationDefinition = request.getPresentationDefinition();
        if (presentationDefinition != null && !presentationDefinition.isEmpty() && isValidJson(presentationDefinition)) {
            return Optional.of(presentationDefinition);
        } else if (request.getClaims() != null && !request.getClaims().isEmpty()) {
            return Optional.of(getUriEncodedSIOPClaims(request, idGenerator));
        }
        return Optional.empty();
    }

    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }

    private static String getUriEncodedSIOPScope(SIOPLoginRequest request) {
        String siopScopes = request.getScopes()
                .stream()
                .map(SIOPScope::getValue)
                .collect(Collectors.joining(" "));
        return UriUtils.encode("openid " + siopScopes, UTF_8);
    }

    private String getUriEncodedSIOPClaims(SIOPLoginRequest request, IdGenerator idGenerator) {

        SIOPPresentationDefinition presentationDefinition = new SIOPPresentationDefinition();

        presentationDefinition.setId(idGenerator.generateId().toString());

        request.getClaims()
                .forEach(siopClaim -> {
                    SIOPInputDescriptor inputDescriptor = new SIOPInputDescriptor();
                    inputDescriptor.setId(siopClaim.getValue());
                    SIOPConstraints constraints = new SIOPConstraints();
                    inputDescriptor.setConstraints(constraints);
                    SIOPField field = new SIOPField();
                    field.getPath().add("$.type");
                    SIOPFilter filter = new SIOPFilter();
                    filter.setType("array");
                    filter.getConstraints().add("VerifiableCredential");
                    filter.getConstraints().add("DvvCoreId");
                    filter.getConstraints().add(siopClaim.getValue());
                    field.setFilter(filter);
                    constraints.getFields().add(field);
                    presentationDefinition.getInputDescriptors().add(inputDescriptor);
                });
        try {
            String jsonString = objectMapper.writeValueAsString(presentationDefinition);

            return String.join(" ", jsonString);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return "";
        }
    }
}
