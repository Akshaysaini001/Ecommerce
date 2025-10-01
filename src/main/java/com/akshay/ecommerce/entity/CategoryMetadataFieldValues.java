package com.akshay.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "category_metadata_field_value")
public class CategoryMetadataFieldValues {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_field_id", nullable = false)
    private CategoryMetadataField categoryMetadataField;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "field_actual_values", // Iske liye ek alag table banegi
            joinColumns = @JoinColumn(name = "field_value_id")
    )
    @Column(name = "value")
    private List<String> values;

}


