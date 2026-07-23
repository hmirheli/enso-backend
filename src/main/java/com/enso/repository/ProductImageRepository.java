package com.enso.repository;

import com.enso.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
}
