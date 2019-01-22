package de.kasperczyk.dinnerdiary.account;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
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
}
