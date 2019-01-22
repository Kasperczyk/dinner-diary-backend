package de.kasperczyk.dinnerdiary.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DISH")
@AttributeOverride(name = "id", column = @Column(name = "DISH_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Dish extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID")
    private Recipe recipe;

    @Column(name = "TITLE")
    private String title;
}
