package com.enso.repository;

import com.enso.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByIdAndDeletedFalse(Long productId);

    Optional<ProductEntity> findBySlugAndDeletedFalse(String slug);

    List<ProductEntity> findByDeletedFalse();

    List<ProductEntity> findByCategory_Name(String categoryName);

    List<ProductEntity> findByDeletedFalseAndCategory_Id(Long categoryId);

    List<ProductEntity> findByDeletedFalseAndNameContainingIgnoreCase(String name);

    List<ProductEntity> findByStockGreaterThan(int stock);
}
