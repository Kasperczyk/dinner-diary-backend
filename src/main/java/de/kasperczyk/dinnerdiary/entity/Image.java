package de.kasperczyk.dinnerdiary.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "IMAGE")
@AttributeOverride(name = "id", column = @Column(name = "IMAGE_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Image extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID")
    private Recipe recipeId;
}
