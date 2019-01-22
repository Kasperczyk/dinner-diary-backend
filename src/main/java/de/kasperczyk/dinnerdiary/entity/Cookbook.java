package de.kasperczyk.dinnerdiary.entity;

import lombok.*;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "COOKBOOK")
@AttributeOverride(name = "id", column = @Column(name = "COOKBOOK_ID"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Cookbook extends AbstractEntity {

    @Column(name = "TITLE")
    private String title;

    @Column(name = "NUMBER_OF_PAGES")
    private Integer numberOfPages;

    @Column(name = "COVER_IMAGE_URL")
    private String coverImageUrl;
}
