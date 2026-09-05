package com.enso.mapper;

import com.enso.dto.response.OrderItemResponse;
import com.enso.dto.response.OrderResponse;
import com.enso.entity.OrderEntity;
import com.enso.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    OrderResponse toOrderResponse(OrderEntity order);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponse toOrderItemResponse(OrderItemEntity orderItem);
}
