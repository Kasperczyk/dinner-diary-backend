package de.kasperczyk.dinnerdiary.error;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String emailAddress) {
        super("Account with email address '" + emailAddress + "' not found");
    }

    public AccountNotFoundException(UUID id) {
        super("Account with id '" + id + "' not found");
    }
}
