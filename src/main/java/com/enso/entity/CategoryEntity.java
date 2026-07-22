package com.enso.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductEntity> productEntities = new ArrayList<>();

    public void addProduct(ProductEntity productEntity) {
        productEntities.add(productEntity);
        productEntity.setCategoryEntity(this);
    }

    public void removeProduct(ProductEntity productEntity) {
        productEntities.remove(productEntity);
        productEntity.setCategoryEntity(null);
    }
}
