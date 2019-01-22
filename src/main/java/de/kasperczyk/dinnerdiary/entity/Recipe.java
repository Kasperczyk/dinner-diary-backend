package de.kasperczyk.dinnerdiary.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "RECIPE")
@AttributeOverride(name = "id", column = @Column(name = "RECIPE_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Recipe extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COOKBOOK_ID")
    private Cookbook cookbook;

    @Column(name = "PAGE")
    private Integer page;

    @Column(name = "INGREDIENT_LIST")
    private String ingredientList;

    @Column(name = "INSTRUCTION_TEXT")
    private String instructionText;
}
