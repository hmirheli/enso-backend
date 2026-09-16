package com.enso.repository;

import com.enso.entity.OrderEntity;
import com.enso.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrder(OrderEntity order);

    Optional<PaymentEntity> findByAuthority(String authority);
}
