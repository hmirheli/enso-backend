package com.enso.entity;

import com.enso.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String recipientMobile;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    private String paymentReference;

    private String trackingCode;

    public void addOrderItem(OrderItemEntity item) {
        orderItemEntities.add(item);
        item.setOrderEntity(this);
    }

    public void removeOrderItem(OrderItemEntity item) {
        orderItemEntities.remove(item);
        item.setOrderEntity(null);
    }
}
