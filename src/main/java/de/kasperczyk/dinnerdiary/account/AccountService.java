package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import de.kasperczyk.dinnerdiary.error.EmailAddressAlreadyInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.accountRepository = accountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    void registerAccount(Account account) {
        if (accountRepository.existsByEmailAddress(account.getEmailAddress())) {
            throw new EmailAddressAlreadyInUseException(account.getEmailAddress());
        }
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    public void updateLastLoginTimestamp(String emailAddress, LocalDateTime lastLoginTimestamp) {
        Account account = accountRepository.findByEmailAddress(emailAddress).orElseThrow(() -> new AccountNotFoundException(emailAddress));
        account.setLastLoginTimestamp(lastLoginTimestamp);
        accountRepository.save(account);
    }

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
