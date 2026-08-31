package com.enso.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {

    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal totalAmount;
}
