package de.kasperczyk.dinnerdiary.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updateTimestamp;

}
