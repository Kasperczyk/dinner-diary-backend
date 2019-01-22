package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.AbstractDataJpaTest;
import de.kasperczyk.dinnerdiary.TestDataFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static de.kasperczyk.dinnerdiary.account.AccountConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AccountRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void existsByEmailAddress_returnsTrue_whenAccountWithEmailAddressExistsInDb() {
        persistAccount();
        assertThat(accountRepository.existsByEmailAddress(EMAIL_ADDRESS), is(true));
    }

    @Test
    public void existsByEmailAddress_returnsFalse_whenAccountWithEmailAddressNotExistsInDb() {
        assertThat(accountRepository.existsByEmailAddress(EMAIL_ADDRESS), is(false));
    }

    @Test
    public void findByEmailAddress_returnsOptionalWithAccount_whenAccountWithEmailAddressExistsInDb() {
        Account account = persistAccount();
        assertThat(accountRepository.findByEmailAddress(EMAIL_ADDRESS).get(), is(account));
    }

    @Test
    public void findByEmailAddress_returnsEmptyOptional_whenAccountWithEmailAddressNotExistsInDb() {
        assertThat(accountRepository.findByEmailAddress(EMAIL_ADDRESS).isPresent(), is(false));
    }

    private Account persistAccount() {
        Account account = TestDataFactory.createTestAccount(USERNAME, EMAIL_ADDRESS, PASSWORD, LocalDateTime.now());
        return testEntityManager.persistAndFlush(account);
    }
}
