package com.enso.repository;

import com.enso.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory(String category);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    List<ProductEntity> findByStockGreaterThan(int stock);
}
