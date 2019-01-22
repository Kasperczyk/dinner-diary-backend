package de.kasperczyk.dinnerdiary.error;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@RequiredArgsConstructor
public class ApiErrorDto {

    private final Integer httpStatusCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
    private String verboseErrorMessage;

    @SuppressWarnings("unused")
    public String getVerboseErrorMessage() {
        return String.format("%s: %s", httpStatus, errorMessage);
    }
}
