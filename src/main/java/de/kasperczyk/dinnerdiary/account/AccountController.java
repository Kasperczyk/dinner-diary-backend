package de.kasperczyk.dinnerdiary.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.kasperczyk.dinnerdiary.error.MalformedJsonPatchException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(AccountService accountService,
                             ModelMapper modelMapper,
                             ObjectMapper objectMapper,
                             BCryptPasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<Void> register(@Valid @RequestBody AccountDto accountDto) {
        LOG.info("Received POST request to register account: " + accountDto);
        accountService.registerAccount(modelMapper.map(accountDto, Account.class));
        LOG.info("Account '" + accountDto + "' successully registered -> returning 201 CREATED");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable UUID accountId) {
        LOG.info("Received GET request for account with id: " + accountId);
        Account account = accountService.getAccount(accountId);
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        LOG.info("Account '" + accountDto + "' found -> returning 200 OK and body");
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID accountId) {
        LOG.info("Received DELETE request for account with id: " + accountId);
        accountService.deleteAccount(accountId);
        LOG.info("Account with accountId '" + accountId + "' successfully deleted -> returning 200 OK");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<Void> patchAccount(@PathVariable UUID accountId,
                                             @RequestBody String patchBody,
                                             @RequestParam(name = "currentPassword", required = false) String currentPassword) throws IOException {
        if (isPasswordPatch(currentPassword)) {
            return patchPassword(accountId, patchBody, currentPassword);
        } else {
            return patchUsernameAndEmailAddress(accountId, patchBody);
        }
    }

    private boolean isPasswordPatch(String currentPassword) {
        return currentPassword != null;
    }

    private ResponseEntity<Void> patchPassword(UUID accountId, String patchBody, String currentPassword) throws IOException {
        LOG.info("Received PATCH request to update password for account with id: " + accountId);
        String patchBodyWithEncodedPassword = replaceRawWithEncodedPassword(patchBody);
        Patch patch = createPatchFromBody(patchBodyWithEncodedPassword, accountId);
        accountService.patchPassword(accountId, patch, currentPassword);
        LOG.info("Successfully patched password for account with accountId '" + accountId + "' -> returning 200 OK");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String replaceRawWithEncodedPassword(String patchBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(patchBody).get(0);
        String newPasswordRaw = jsonNode.findValue("value").asText();
        String newPasswordEncoded = passwordEncoder.encode(newPasswordRaw);
        ((ObjectNode)jsonNode).put("value", newPasswordEncoded);
        return objectMapper.writeValueAsString(new ArrayNode(JsonNodeFactory.instance, Collections.singletonList(jsonNode)));
    }

    private ResponseEntity<Void> patchUsernameAndEmailAddress(UUID accountId, String patchBody) throws IOException {
        LOG.info("Received PATCH request to update username and emailAddress for account with id: " + accountId);
        Patch patch = createPatchFromBody(patchBody, accountId);
        objectMapper.readTree(patchBody).forEach(jsonNode -> {
            if (jsonNode.findValue("path").asText().equals("/emailAddress")) {
                accountService.patchUsernameAndEmailAddress(accountId, patch, jsonNode.findValue("value").asText());
            }
        });
        LOG.info("Successfully patched username and email address for account with accountId '" + accountId + "' -> returning 200 OK");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Patch createPatchFromBody(String patchBody, UUID accountId) {
        try {
            return new JsonPatchPatchConverter(objectMapper).convert(objectMapper.readTree(patchBody));
        } catch (IOException e) {
            throw new MalformedJsonPatchException(accountId);
        }
    }
}
