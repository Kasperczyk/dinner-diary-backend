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

public class AccountDtoTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void newAccountDto_isInvalid_whenUsernameIsNull() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(null, EMAIL_ADDRESS, PASSWORD, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_USERNAME, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenUsernameIsEmpty() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto("", EMAIL_ADDRESS, PASSWORD, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_USERNAME, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDtoDto_isInvalid_whenEmailAddressIsNull() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, null, PASSWORD, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenEmailAddressIsEmpty() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, "", PASSWORD, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenEmailAddressIsNotWellFormed() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, "kasperczyk.rene", PASSWORD, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_EMAIL_ADDRESS, EMAIL_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenPasswordIsNull() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, EMAIL_ADDRESS, null, LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_PASSWORD, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenPasswordIsEmpty() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, EMAIL_ADDRESS, "", LocalDateTime.now());
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_PASSWORD, NOT_BLANK_MESSAGE);
    }

    @Test
    public void newAccountDto_isInvalid_whenLastLoginTimestampIsInFuture() {
        AccountDto accountDto = TestDataFactory.createTestAccountDto(USERNAME, EMAIL_ADDRESS, PASSWORD, LocalDateTime.now().plusSeconds(1));
        ConstraintViolation<AccountDto> violation = validator.validate(accountDto).iterator().next();
        assertViolation(violation, PATH_LAST_LOGIN_TIMESTAMP, PAST_MESSAGE);
    }

    private void assertViolation(ConstraintViolation<AccountDto> violation, String path, String message) {
        assertThat(violation.getPropertyPath().toString(), is(path));
        assertThat(violation.getMessage(), is(message));
    }
}
