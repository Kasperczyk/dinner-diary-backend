package de.kasperczyk.dinnerdiary.error;

import java.util.UUID;

public class MalformedJsonPatchException extends RuntimeException {

    public MalformedJsonPatchException(UUID accountId) {
        super("Error while creating patch for user profile of user with externalId '" + accountId + "'");
    }
}
