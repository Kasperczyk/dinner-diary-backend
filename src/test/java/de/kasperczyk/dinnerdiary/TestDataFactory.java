package de.kasperczyk.dinnerdiary;

import de.kasperczyk.dinnerdiary.account.Account;
import de.kasperczyk.dinnerdiary.account.AccountDto;

import java.time.LocalDateTime;

import static de.kasperczyk.dinnerdiary.account.AccountConstants.*;

public abstract class TestDataFactory {

    public static Account createTestAccount(String username, String emailAddress, String password, LocalDateTime lastLoginTimestamp) {
        return Account.builder()
                .username(username)
                .emailAddress(emailAddress)
                .password(password)
                .lastLoginTimestamp(lastLoginTimestamp)
                .build();
    }

    public static Account createDefaultTestAccount() {
        return Account.builder()
                .username(USERNAME)
                .emailAddress(EMAIL_ADDRESS)
                .password(PASSWORD)
                .lastLoginTimestamp(LocalDateTime.now())
                .build();
    }

    public static AccountDto createTestAccountDto(String username, String emailAddress, String password, LocalDateTime lastLoginTimestamp) {
        return AccountDto.builder()
                .username(username)
                .emailAddress(emailAddress)
                .password(password)
                .lastLoginTimestamp(lastLoginTimestamp)
                .build();
    }

    public static AccountDto createDefaultTestAccountDto() {
        return AccountDto.builder()
                .username(USERNAME)
                .emailAddress(EMAIL_ADDRESS)
                .password(PASSWORD)
                .lastLoginTimestamp(LocalDateTime.now())
                .build();
    }
}
