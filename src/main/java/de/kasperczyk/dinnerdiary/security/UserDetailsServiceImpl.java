package de.kasperczyk.dinnerdiary.security;

import de.kasperczyk.dinnerdiary.account.Account;
import de.kasperczyk.dinnerdiary.account.AccountRepository;
import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        return accountRepository.findByEmailAddress(emailAddress).orElseThrow(() -> new AccountNotFoundException(emailAddress));
    }
}
