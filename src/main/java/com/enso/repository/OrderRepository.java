package com.enso.repository;

import com.enso.entity.OrderEntity;
import com.enso.entity.UserEntity;
import com.enso.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUser(UserEntity userEntity);

    List<OrderEntity> findByUserOrderByCreatedAtDesc(UserEntity userEntity);

    List<OrderEntity> findByStatus(OrderStatus status);

    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderItems i JOIN FETCH i.product WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(Long id);

    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderItems i JOIN FETCH i.product WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserWithItems(UserEntity userEntity);
}
