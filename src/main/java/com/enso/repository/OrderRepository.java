package com.enso.repository;

import com.enso.entity.OrderEntity;
import com.enso.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    Optional<OrderEntity> findByIdAndUser(Long id, UserEntity user);
}
