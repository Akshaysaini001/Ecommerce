package com.akshay.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
public class CategoryMetadataField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;

@OneToMany(mappedBy = "categoryMetadataField", cascade = CascadeType.ALL)
private List<CategoryMetadataFieldValues> categoryFieldValues = new ArrayList<>();


    public void addCategoryFieldValue(CategoryMetadataFieldValues value) {
        this.categoryFieldValues.add(value);
        value.setCategoryMetadataField(this);
    }

    public void removeCategoryFieldValue(CategoryMetadataFieldValues value) {
        this.categoryFieldValues.remove(value);
        value.setCategoryMetadataField(null);
    }


}

