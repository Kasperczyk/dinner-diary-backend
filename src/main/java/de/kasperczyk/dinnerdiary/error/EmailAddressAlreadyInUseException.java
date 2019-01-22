package de.kasperczyk.dinnerdiary.error;

public class EmailAddressAlreadyInUseException extends RuntimeException {

    public EmailAddressAlreadyInUseException(String emailAddress) {
        super("Email address '" + emailAddress + "' is already in use");
    }
}
