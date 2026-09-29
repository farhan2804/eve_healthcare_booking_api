package com.evehealthcare.eve_healthcare_booking_api.repository;

import com.evehealthcare.eve_healthcare_booking_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByWebhookEventId(String webhookEventId);
}