package com.enso.mapper;

import com.enso.dto.response.CartItemResponse;
import com.enso.dto.response.CartResponse;
import com.enso.entity.CartEntity;
import com.enso.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "totalItems", expression = "java(calculateTotalItems(cart))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(cart))")
    CartResponse toCartResponce(CartEntity cart);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.category.name", target = "productCategory")
    @Mapping(target = "subtotal", expression = "java(cartItem.getSubtotal())")
    @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(cartItem))")
    CartItemResponse toCartItemResponce(CartItemEntity cartItem);

    default int calculateTotalItems(CartEntity cart) {
        return cart.getItems().stream().mapToInt(CartItemEntity::getQuantity).sum();
    }

    default BigDecimal calculateTotalAmount(CartEntity cart) {
        return cart.getItems().stream().map(CartItemEntity::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String getFirstImageUrl(CartItemEntity item) {
        if (item.getProduct().getImages() == null || item.getProduct().getImages().isEmpty()) {
            return null;
        }
        return item.getProduct().getImages().get(0).getImageUrl();
    }
}
