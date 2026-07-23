package com.enso.repository;

import com.enso.entity.CartEntity;
import com.enso.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByUser(UserEntity userEntity);

    @Query("SELECT c FROM CartEntity c JOIN FETCH c.items i JOIN FETCH i.product WHERE c.user = :user")
    Optional<CartEntity> findByUserWithItems(UserEntity user);
}
