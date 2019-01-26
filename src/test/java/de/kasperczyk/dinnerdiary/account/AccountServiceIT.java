package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.TestDataFactory;
import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import de.kasperczyk.dinnerdiary.error.EmailAddressAlreadyInUseException;
import de.kasperczyk.dinnerdiary.error.IncorrectPasswordException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static de.kasperczyk.dinnerdiary.account.AccountConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AccountServiceIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoderMock;

    @Test
    public void registerAccount_savesAccountWithEncodedPassword() {
        when(bCryptPasswordEncoderMock.encode(PASSWORD)).thenReturn("ENCODED_PASSWORD");
        Account account = TestDataFactory.createDefaultTestAccount();
        accountService.registerAccount(account);
        verify(bCryptPasswordEncoderMock).encode(PASSWORD);
        assertThat(accountRepository.findByEmailAddress(EMAIL_ADDRESS).get(), is(account));
    }

    @Test
    public void registerAccount_throwsEmailAddressAlreadyInUseException_whenEmailAddressIsAlreadyInUse() {
        when(bCryptPasswordEncoderMock.encode(PASSWORD)).thenReturn("ENCODED_PASSWORD");
        expectedException.expect(EmailAddressAlreadyInUseException.class);
        accountService.registerAccount(TestDataFactory.createDefaultTestAccount());
        accountService.registerAccount(TestDataFactory.createDefaultTestAccount());
    }

    @Test
    public void updateLastLoginTimestamp_updatesAccountWithLastLoginTimestamp_whenAccountExistsInDb() {
        when(bCryptPasswordEncoderMock.encode(PASSWORD)).thenReturn("ENCODED_PASSWORD");
        Account account = TestDataFactory.createDefaultTestAccount();
        accountService.registerAccount(account);
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertThat(account.getLastLoginTimestamp(), is(not(pastDate)));
        accountService.updateLastLoginTimestamp(EMAIL_ADDRESS, pastDate);
        assertThat(accountRepository.findByEmailAddress(EMAIL_ADDRESS).get().getLastLoginTimestamp(), is(pastDate));
    }

    @Test
    public void updateLastLoginTimestamp_throwsAccountNotFoundException_whenAccountNotExistsInDb() {
        expectedException.expect(AccountNotFoundException.class);
        accountService.updateLastLoginTimestamp(EMAIL_ADDRESS, LocalDateTime.now());
    }

    @Test
    public void getAccount_returnsAccount_whenAccountExistsInDb() {
        when(bCryptPasswordEncoderMock.encode(PASSWORD)).thenReturn("ENCODED_PASSWORD");
        Account account = TestDataFactory.createDefaultTestAccount();
        accountService.registerAccount(account);
        UUID id = accountRepository.findByEmailAddress(EMAIL_ADDRESS).get().getId();
        assertThat(accountService.getAccount(id), is(account));
    }

    @Test
    public void getAccount_throwsAccountNotFoundException_whenAccountNotExistsInDb() {
        expectedException.expect(AccountNotFoundException.class);
        accountService.getAccount(UUID.randomUUID());
    }

    @Test
    public void deleteAccount_deletesAccount_whenAccountExistsInDb() {
        Account account = TestDataFactory.createDefaultTestAccount();
        accountRepository.save(account);
        assertThat(accountRepository.existsById(account.getId()), is(true));
        accountService.deleteAccount(account.getId());
        assertThat(accountRepository.existsById(account.getId()), is(false));
    }

    @Test
    public void deleteAccount_throwsAccountNotFoundException_whenAccountNotExistsInDb() {
        expectedException.expect(AccountNotFoundException.class);
        accountService.deleteAccount(UUID.randomUUID());
    }

    @Test
    public void patchUsernameAndEmailAddress_patchesAccount() {
        Account account = TestDataFactory.createDefaultTestAccount();
        accountRepository.save(account);
        Patch patchMock = mock(Patch.class);
        accountService.patchUsernameAndEmailAddress(account.getId(), patchMock, "kasperczyk.rene@test.com");
        // todo no real check here except that no exception is thrown
    }

    @Test
    public void patchUsernameAndEmailAddress_throwsAccountNotFoundException_whenAccountNotExistsInDb() {
        expectedException.expect(AccountNotFoundException.class);
        accountService.patchUsernameAndEmailAddress(UUID.randomUUID(), new Patch(null), EMAIL_ADDRESS);
    }

    @Test
    public void patchUsernameAndEmailAddress_throwsEmailAddressAlreadyInUse_whenEmailAddressIsAlreadyInUse() {
        Account account = TestDataFactory.createDefaultTestAccount();
        Account anotherAccount = TestDataFactory.createTestAccount(USERNAME, "kasperczyk.rene@test.com", PASSWORD, LocalDateTime.now());
        accountRepository.save(account);
        accountRepository.save(anotherAccount);
        expectedException.expect(EmailAddressAlreadyInUseException.class);
        accountService.patchUsernameAndEmailAddress(account.getId(), new Patch(null), anotherAccount.getEmailAddress());
    }

    @Test
    public void patchUsernameAndEmailAddress_doesNotThrowEmailAddressAlreadyInUseException_whenItIsTheUsersOwnEmailAddress() {
        Account account = TestDataFactory.createDefaultTestAccount();
        accountRepository.save(account);
        Patch patchMock = mock(Patch.class);
        accountService.patchUsernameAndEmailAddress(account.getId(), patchMock, account.getEmailAddress());
    }

    @Test
    public void patchPassword_patchesAccount() {
        Account account = TestDataFactory.createDefaultTestAccount();
        accountRepository.save(account);
        when(bCryptPasswordEncoderMock.matches(any(CharSequence.class), any(String.class))).thenReturn(true);
        Patch patchMock = mock(Patch.class);
        accountService.patchPassword(account.getId(), patchMock, account.getPassword());
        // todo no real check here except that no exception is thrown
    }

    @Test
    public void patchPassword_throwsAccountNotFoundException_whenAccountNotExistsInDb() {
        expectedException.expect(AccountNotFoundException.class);
        accountService.patchPassword(UUID.randomUUID(), new Patch(null), "anyPassword");
    }

    @Test
    public void patchPassword_throwsIncorrectPasswordException_whenIncorrectPasswordIsPassed() {
        Account account = TestDataFactory.createDefaultTestAccount();
        accountRepository.save(account);
        when(bCryptPasswordEncoderMock.matches(any(CharSequence.class), any(String.class))).thenReturn(false);
        expectedException.expect(IncorrectPasswordException.class);
        accountService.patchPassword(account.getId(), new Patch(null), "anyPassword");
    }
}