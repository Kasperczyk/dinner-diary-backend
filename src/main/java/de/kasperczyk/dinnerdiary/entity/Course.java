package de.kasperczyk.dinnerdiary.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "COURSE")
@AttributeOverride(name = "id", column = @Column(name = "COURSE_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Course extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISH_ID")
    private Dish dish;

    @Column(name = "COURSE_NUMBER")
    @NotNull
    private Integer courseNumber;
}
