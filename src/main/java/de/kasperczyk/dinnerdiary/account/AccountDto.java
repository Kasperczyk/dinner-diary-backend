package de.kasperczyk.dinnerdiary.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private UUID id;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String emailAddress;

    @NotBlank
    private String password;

    @PastOrPresent
    private LocalDateTime lastLoginTimestamp;
}
