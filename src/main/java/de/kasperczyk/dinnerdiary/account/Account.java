package de.kasperczyk.dinnerdiary.account;

import de.kasperczyk.dinnerdiary.entity.AbstractEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "ACCOUNT")
@AttributeOverride(name = "id", column = @Column(name = "ACCOUNT_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Account extends AbstractEntity implements UserDetails {

    @Column(name = "USERNAME")
    @NotBlank
    private String username;

    @Column(name = "EMAIL_ADDRESS")
    @NotBlank
    @Email
    private String emailAddress;

    @Column(name = "PASSWORD")
    @NotBlank
    private String password;

    @Column(name = "LAST_LOGIN_TIMESTAMP")
    @PastOrPresent
    private LocalDateTime lastLoginTimestamp;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
