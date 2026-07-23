package com.enso.repository;

import com.enso.entity.CartEntity;
import com.enso.entity.CartItemEntity;
import com.enso.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByCartAndProduct(CartEntity cartEntity, ProductEntity productEntity);

    void deleteByCartAndProduct(CartEntity cartEntity, ProductEntity productEntity);
}
