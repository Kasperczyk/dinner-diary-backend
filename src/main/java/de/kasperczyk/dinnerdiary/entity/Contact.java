package de.kasperczyk.dinnerdiary.entity;

import de.kasperczyk.dinnerdiary.account.Account;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CONTACT")
@AttributeOverride(name = "id", column = @Column(name = "CONTACT_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Contact extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Account account;

    private String emailAddress;
}
