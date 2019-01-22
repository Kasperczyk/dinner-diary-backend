package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.TestDataFactory;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;

import static de.kasperczyk.dinnerdiary.account.AccountConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void newAccount_isInvalid_whenUsernameIsNull() {
        Account account = TestDataFactory.createTestAccount(null, EMAIL_ADDRESS, PASSWORD, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_USERNAME, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenUsernameIsEmpty() {
        Account account = TestDataFactory.createTestAccount("", EMAIL_ADDRESS, PASSWORD, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_USERNAME, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenEmailAddressIsNull() {
        Account account = TestDataFactory.createTestAccount(USERNAME, null, PASSWORD, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenEmailAddressIsEmpty() {
        Account account = TestDataFactory.createTestAccount(USERNAME, "", PASSWORD, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenEmailAddressIsNotWellFormed() {
        Account account = TestDataFactory.createTestAccount(USERNAME, "kasperczyk.rene", PASSWORD, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, EMAIL_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenPasswordIsNull() {
        Account account = TestDataFactory.createTestAccount(USERNAME, EMAIL_ADDRESS, null, LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_PASSWORD, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenPasswordIsEmpty() {
        Account account = TestDataFactory.createTestAccount(USERNAME, EMAIL_ADDRESS, "", LocalDateTime.now());
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_PASSWORD, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccount_isInvalid_whenLastLoginTimestampIsInFuture() {
        Account account = TestDataFactory.createTestAccount(USERNAME, EMAIL_ADDRESS, PASSWORD, LocalDateTime.now().plusSeconds(1));
        ConstraintViolation<Account> violation = validator.validate(account).iterator().next();
        assertViolation(violation, PATH_LAST_LOGIN_TIMESTAMP, PAST_MESSAGE);
    }

    private void assertViolation(ConstraintViolation<Account> violation, String path, String message) {
        assertThat(violation.getPropertyPath().toString(), is(path));
        assertThat(violation.getMessage(), is(message));
    }
}
