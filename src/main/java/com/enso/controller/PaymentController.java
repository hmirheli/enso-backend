package com.enso.controller;

import com.enso.config.AppConfig;
import com.enso.dto.request.CreatePaymentRequest;
import com.enso.dto.response.PaymentResponse;
import com.enso.enums.PaymentStatus;
import com.enso.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AppConfig appConfig;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyPayment(@RequestParam String authority) {
        paymentService.verifyPayment(authority);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zarinpal/callback")
    public ResponseEntity<Void> zarinpalCallback(@RequestParam String Authority) {
        paymentService.verifyPayment(Authority);

        PaymentStatus paymentStatus = paymentService.getPaymentStatus(Authority);

        String redirectUrl = appConfig.getFrontendUrl()
                + "/payment/result?status="
                + paymentStatus.name();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }
}
