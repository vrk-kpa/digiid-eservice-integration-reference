package fi.dvv.digiid.ho.op.restservice.rest.siop;

import fi.dvv.digiid.ho.op.restservice.config.RestServiceConfiguration;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationBadRequestException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPAuthenticationFailedException;
import fi.dvv.digiid.ho.op.restservice.domain.exceptions.siop.SIOPTransactionNotFoundException;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPAuthRequest;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPAuthResponse;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPLoginRequest;
import fi.dvv.digiid.ho.op.restservice.domain.siop.SIOPTransactionStatusResponse;
import fi.dvv.digiid.ho.op.restservice.service.siop.repository.SIOPTransaction;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPAuthenticationService;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPLoginService;
import fi.dvv.digiid.ho.op.restservice.service.siop.service.SIOPTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RequestMapping(path = "/siop/api/1.0")
@RestController
public class SIOPController {

    private final RestServiceConfiguration configuration;
    private final SIOPLoginService loginService;

    private final SIOPAuthenticationService authenticationService;

    private final SIOPTransactionService statusService;

    @Autowired
    public SIOPController(RestServiceConfiguration configuration, SIOPLoginService loginService,
                          SIOPAuthenticationService authenticationService, SIOPTransactionService statusService) {
        this.configuration = configuration;
        this.loginService = loginService;
        this.authenticationService = authenticationService;
        this.statusService = statusService;
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> login(@Valid @RequestBody SIOPLoginRequest request) {
        try {
            String uriString = loginService.handleLogin(request, configuration.getSiopRedirect());
            return ResponseEntity.created(new URI(uriString)).body(uriString);
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Constructed invalid login URI");
        }

    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/presentationdef/{nonce}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPresentationDefinition(@PathVariable String nonce) {
        try {
            return ResponseEntity.ok().body(statusService.getTransaction(nonce).getPresentationDefinition());
        } catch (SIOPTransactionNotFoundException e) {
            log.warn("SIOP transaction not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(path = "/status/{nonce}")
    public ResponseEntity<SIOPTransactionStatusResponse> status(@PathVariable String nonce) {
        try {
            SIOPTransaction transaction = statusService.getTransactionAndDeleteIfReady(nonce);
            SIOPTransactionStatusResponse response = SIOPTransactionStatusResponse.builder()
                    .status(transaction.getStatus())
                    .error(transaction.getError())
                    .credentials(transaction.getCredentials())
                    .build();
            return ResponseEntity.ok().body(response);
        } catch (SIOPTransactionNotFoundException e) {
            log.error("SIOP transaction not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping(path = "/auth", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SIOPAuthResponse> auth(@Valid SIOPAuthRequest request) {
        try {
            if (request.getError() != null) {
                authenticationService.handleAuthError(request);
            } else {
                authenticationService.handleAuth(request);
            }
            return ResponseEntity.ok().body(new SIOPAuthResponse("OK"));
        } catch (SIOPAuthenticationFailedException | SIOPTransactionNotFoundException | SIOPAuthenticationException e) {
            log.error("SIOP authentication error", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (SIOPAuthenticationBadRequestException e) {
            log.error("SIOP authentication bad request", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            if (!(e instanceof ResponseStatusException)) {
                log.error("Error occurred", e);
            }
            throw e;
        }
    }
}
