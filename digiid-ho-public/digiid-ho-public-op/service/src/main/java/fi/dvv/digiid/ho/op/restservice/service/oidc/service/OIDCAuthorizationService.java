package fi.dvv.digiid.ho.op.restservice.service.oidc.service;

import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.oidc.OIDCTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCAuthRequest;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCTransactionStatus;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.OIDCTransactionStatusResponse;
import fi.dvv.digiid.ho.op.restservice.domain.oidc.UILocale;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPAuthRequest;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPClaim;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPLoginRequest;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCJwtPublicKeyRepository;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCTransaction;
import fi.dvv.digiid.ho.op.restservice.service.oidc.repository.OIDCTransactionRepository;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPAuthenticationService;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPLoginService;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OIDCAuthorizationService extends OIDCServiceBase {

    @Value("${fi.dvv.digiid.op.oidcSiopUi}")
    private String siopUi;

    @Value("${fi.dvv.digiid.op.siopRedirect}")
    private String siopRedirect;

    private final OIDCTransactionRepository transactionRepository;

    private final OIDCValidationService validationService;
    private final Clock clock;
    private final SIOPAuthenticationService siopAuthenticationService;
    private final SIOPTransactionService siopTransactionService;
    private final SIOPLoginService siopLoginService;

    @Autowired
    public OIDCAuthorizationService(OIDCConfiguration config, OIDCTransactionRepository transactionRepository,
                                    OIDCValidationService validationService, OIDCJwtPublicKeyRepository jwkCache, Clock clock,
                                    SIOPAuthenticationService siopAuthenticationService, SIOPTransactionService siopTransactionService,
                                    SIOPLoginService siopLoginService, IdGenerator idGenerator) {
        super(config, jwkCache, idGenerator);
        this.transactionRepository = transactionRepository;
        this.validationService = validationService;
        this.clock = clock;
        this.siopAuthenticationService = siopAuthenticationService;
        this.siopTransactionService = siopTransactionService;
        this.siopLoginService = siopLoginService;
    }

    public String authorize(OIDCAuthRequest request) throws OIDCAuthenticationBadRequestException {
        OIDCConfiguration.OidcClient client = validationService.getAndValidateClient(request);
        String authTransaction = idGenerator.generateId().toString();
        String code = performSIOPLogin(authTransaction);

        OIDCTransaction transaction = OIDCTransaction.builder()
                .code(authTransaction)
                .timestamp(clock.millis())
                .status(OIDCTransactionStatus.CREATED)
                .client(client)
                .redirectUri(request.getRedirectUri())
                .nonce(request.getNonce())
                .state(request.getState())
                .acr(request.getAcrValues())
                .build();

        transactionRepository.save(transaction);

        String uiLocales = request.getUiLocales()
                .stream()
                .map(UILocale::getCode)
                .collect(Collectors.joining(" "));

        return UriComponentsBuilder.fromUriString(siopUi)
                .queryParam("code", URLEncoder.encode(Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8))
                .queryParam("ftn_spname", URLEncoder.encode(request.getFtnSpname(), StandardCharsets.UTF_8))
                .queryParam("ui_locales", URLEncoder.encode(uiLocales, StandardCharsets.UTF_8))
                .build(true)
                .toUriString();
    }

    private String performSIOPLogin(String authTransaction) {
        List<SIOPClaim> claims = List.of(SIOPClaim.GIVEN_NAME, SIOPClaim.FAMILY_NAME, SIOPClaim.BIRTH_DATE, SIOPClaim.PERSONAL_IDENTITY_CODE, SIOPClaim.LEVEL_OF_ASSURANCE);

        SIOPLoginRequest loginRequest = SIOPLoginRequest.builder()
                .claims(claims)
                .nonce(authTransaction).build();

        return siopLoginService.handleLogin(loginRequest, siopRedirect);
    }

    public String cancel(String code, String error) throws OIDCTransactionNotFoundException, OIDCAuthenticationBadRequestException {
        try {
            SIOPTransaction siopTransaction = siopAuthenticationService.handleAuthError(SIOPAuthRequest.builder().error(error).state(code).build());

            OIDCTransaction transaction = transactionRepository.findById(siopTransaction.getNonce()).orElseThrow(OIDCTransactionNotFoundException::new);

            transaction.setStatus(OIDCTransactionStatus.FAILED);
            transactionRepository.save(transaction);

            return UriComponentsBuilder.fromUriString(transaction.getRedirectUri())
                    .queryParam("state", transaction.getState())
                    .queryParam("error", error)
                    .build(true)
                    .toUriString();

        } catch (SIOPTransactionNotFoundException e) {
            throw new OIDCTransactionNotFoundException();
        } catch (SIOPAuthenticationFailedException | SIOPAuthenticationBadRequestException e) {
            throw new OIDCAuthenticationBadRequestException("SIOP auth error handling failed");
        }
    }

    public OIDCTransactionStatusResponse getStatus(String code) throws OIDCTransactionNotFoundException {
        try {
            SIOPTransaction siopTransaction = siopTransactionService.getTransactionAndDeleteIfReady(code);

            switch (siopTransaction.getStatus()) {
                case FAILED -> {
                    OIDCTransaction transaction = transactionRepository.findById(siopTransaction.getNonce()).orElseThrow(OIDCTransactionNotFoundException::new);
                    return new OIDCTransactionStatusResponse(OIDCTransactionStatus.FAILED, buildRedirectUri(
                            transaction.getRedirectUri(), transaction.getState(), null, siopTransaction.getError()));
                }
                case CREATED -> {
                    return new OIDCTransactionStatusResponse(OIDCTransactionStatus.CREATED, null);
                }
                case READY -> {
                    OIDCTransaction transaction = transactionRepository.findById(siopTransaction.getNonce()).orElseThrow(OIDCTransactionNotFoundException::new);
                    transaction.setStatus(OIDCTransactionStatus.READY);
                    transaction.setCredentials(siopTransaction.getCredentials());
                    transactionRepository.save(transaction);

                    return new OIDCTransactionStatusResponse(transaction.getStatus(), buildRedirectUri(
                            transaction.getRedirectUri(), transaction.getState(), transaction.getCode(), null));
                }
                default -> throw new OIDCTransactionNotFoundException();
            }

        } catch (SIOPTransactionNotFoundException e) {
            throw new OIDCTransactionNotFoundException();
        }
    }

    private String buildRedirectUri(String redirectUriBase, String state, String code, String error) {
        return UriComponentsBuilder.fromUriString(redirectUriBase)
                .queryParamIfPresent("state", Optional.ofNullable(state))
                .queryParamIfPresent("code", Optional.ofNullable(code))
                .queryParamIfPresent("error", Optional.ofNullable(error))
                .build(true)
                .toUriString();
    }
}
