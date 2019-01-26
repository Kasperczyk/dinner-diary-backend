package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import de.kasperczyk.dinnerdiary.error.EmailAddressAlreadyInUseException;
import de.kasperczyk.dinnerdiary.error.IncorrectPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.json.patch.Patch;
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
        checkEmailAvailability(account.getEmailAddress());
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

    public void deleteAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        accountRepository.delete(account);
    }

    public void patchUsernameAndEmailAddress(UUID accountId, Patch patch, String newEmailAddress) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.getEmailAddress().equals(newEmailAddress)) {
            checkEmailAvailability(newEmailAddress);
        }
        patchAccount(account, patch);
    }

    private void checkEmailAvailability(String newEmailAddress) {
        if (accountRepository.existsByEmailAddress(newEmailAddress)) {
            throw new EmailAddressAlreadyInUseException(newEmailAddress);
        }
    }

    public void patchPassword(UUID accountId, Patch patch, String currentPassword) {
        Account account = checkCurrentPassword(accountId, currentPassword);
        patchAccount(account, patch);
    }

    private Account checkCurrentPassword(UUID accountId, String currentPassword) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (bCryptPasswordEncoder.matches(currentPassword, account.getPassword())) {
            return account;
        } else {
            throw new IncorrectPasswordException(accountId);
        }
    }

    private void patchAccount(Account account, Patch patch) {
        patch.apply(account, Account.class);
        accountRepository.save(account);
    }
}
