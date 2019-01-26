package de.kasperczyk.dinnerdiary.error;

import java.util.UUID;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException(UUID accountId) {
        super("Incorrect password provided for account with id: " + accountId);
    }

    IncorrectPasswordException(String emailAddress) {
        super("Incorrect password provided for account with id: " + emailAddress);
    }
}
