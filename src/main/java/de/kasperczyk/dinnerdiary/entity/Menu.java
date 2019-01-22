package de.kasperczyk.dinnerdiary.entity;

import de.kasperczyk.dinnerdiary.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MENU")
@AttributeOverride(name = "id", column = @Column(name = "MENU_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Menu extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Account account;

    @Column(name = "COOKED_ON")
    private LocalDate cookedOn;
}
