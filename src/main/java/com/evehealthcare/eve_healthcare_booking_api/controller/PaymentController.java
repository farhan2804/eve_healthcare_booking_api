package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.dto.PaymentResponse;
import com.evehealthcare.eve_healthcare_booking_api.dto.PaymentWebhookRequest;
import com.evehealthcare.eve_healthcare_booking_api.entity.Payment;
import com.evehealthcare.eve_healthcare_booking_api.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse processPayment(
        @RequestParam Long bookingId,
        @RequestParam boolean success,
        Authentication authentication) {

        Payment payment = paymentService.processPayment(
        bookingId,
        success,
        authentication.getName());

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setBookingId(payment.getBooking().getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setWebhookEventId(payment.getWebhookEventId());
        response.setCreatedAt(payment.getCreatedAt());

        return response;
    }

    @PostMapping("/webhook")
    public PaymentResponse processWebhook(
            @Valid @RequestBody PaymentWebhookRequest request) {

        Payment payment = paymentService.processWebhook(request);

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBooking().getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setWebhookEventId(payment.getWebhookEventId());
        response.setCreatedAt(payment.getCreatedAt());

        return response;
    }
}