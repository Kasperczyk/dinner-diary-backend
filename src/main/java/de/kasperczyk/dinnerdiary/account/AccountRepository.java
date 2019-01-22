package de.kasperczyk.dinnerdiary.account;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {

    boolean existsByEmailAddress(String emailAddress);

    Optional<Account> findByEmailAddress(String emailAddress);
}
