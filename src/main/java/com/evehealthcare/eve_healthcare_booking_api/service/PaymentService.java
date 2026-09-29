package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.dto.PaymentWebhookRequest;
import com.evehealthcare.eve_healthcare_booking_api.entity.Booking;
import com.evehealthcare.eve_healthcare_booking_api.entity.BookingStatus;
import com.evehealthcare.eve_healthcare_booking_api.entity.Payment;
import com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus;
import com.evehealthcare.eve_healthcare_booking_api.repository.BookingRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public Payment processPayment(
            Long bookingId,
            boolean success,
            String userEmail) {

        log.info(
                "Payment request received for booking: {}, user: {}, success: {}",
                bookingId,
                userEmail,
                success);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check whether the authenticated user owns this booking
        if (!booking.getUser().getEmail().equals(userEmail)) {

            log.warn(
                    "Unauthorized payment attempt for booking: {} by user: {}",
                    bookingId,
                    userEmail);

            throw new RuntimeException(
                    "You are not authorized to access this booking");
        }

        // Prevent duplicate payments
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {

            log.warn(
                    "Duplicate payment attempt for booking: {}",
                    bookingId);

            throw new RuntimeException(
                    "Payment already exists for this booking");
        }

        Payment payment = new Payment();

        payment.setBooking(booking);
        payment.setAmount(booking.getAmount());
        payment.setTransactionId(UUID.randomUUID().toString());

        if (success) {

            payment.setStatus(PaymentStatus.SUCCESS);
            booking.setStatus(BookingStatus.CONFIRMED);

        } else {

            payment.setStatus(PaymentStatus.FAILED);
            booking.setStatus(BookingStatus.FAILED);
        }

        bookingRepository.save(booking);

        Payment savedPayment = paymentRepository.save(payment);

        log.info(
                "Payment processed successfully for booking: {}, status: {}",
                bookingId,
                savedPayment.getStatus());

        return savedPayment;
    }

    public Payment processWebhook(PaymentWebhookRequest request) {

        log.info(
                "Payment webhook received. Event: {}, Booking: {}, Status: {}",
                request.getEventId(),
                request.getBookingId(),
                request.getStatus());

        // 1. Same webhook event was already processed
        Optional<Payment> existingEvent = paymentRepository.findByWebhookEventId(
                request.getEventId());

        if (existingEvent.isPresent()) {

            log.info(
                    "Duplicate webhook event ignored: {}",
                    request.getEventId());

            return existingEvent.get();
        }

        // 2. Find booking
        Booking booking = bookingRepository.findById(
                request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 3. Check if payment already exists for this booking
        Payment payment = paymentRepository.findByBookingId(
                request.getBookingId())
                .orElse(null);

        // 4. If this booking already has a processed webhook,
        // don't allow another event to change its final state
        if (payment != null
                && payment.getWebhookEventId() != null) {

            log.info(
                    "Booking {} already has a processed webhook. Ignoring new event: {}",
                    request.getBookingId(),
                    request.getEventId());

            return payment;
        }

        // 5. Create payment if it doesn't exist
        if (payment == null) {

            payment = new Payment();

            payment.setBooking(booking);
            payment.setAmount(booking.getAmount());
            payment.setTransactionId(UUID.randomUUID().toString());
        }

        // 6. Process webhook
        payment.setWebhookEventId(request.getEventId());
        payment.setStatus(request.getStatus());

        if (request.getStatus() == PaymentStatus.SUCCESS) {

            booking.setStatus(BookingStatus.CONFIRMED);

        } else {

            booking.setStatus(BookingStatus.FAILED);
        }

        bookingRepository.save(booking);

        Payment savedPayment = paymentRepository.save(payment);

        log.info(
                "Webhook processed successfully. Event: {}, Booking: {}, Status: {}",
                request.getEventId(),
                request.getBookingId(),
                savedPayment.getStatus());

        return savedPayment;
    }
}