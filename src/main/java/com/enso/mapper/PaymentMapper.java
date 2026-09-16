package com.enso.mapper;

import com.enso.dto.response.PaymentResponse;
import com.enso.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "payment.order.id", target = "orderId")
    @Mapping(source = "paymentUrl", target = "paymentUrl")
    PaymentResponse toPaymentResponse(PaymentEntity payment, String paymentUrl);
}
