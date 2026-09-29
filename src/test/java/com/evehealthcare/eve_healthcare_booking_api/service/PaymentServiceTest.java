package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.dto.PaymentWebhookRequest;
import com.evehealthcare.eve_healthcare_booking_api.entity.Booking;
import com.evehealthcare.eve_healthcare_booking_api.entity.BookingStatus;
import com.evehealthcare.eve_healthcare_booking_api.entity.Payment;
import com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus;
import com.evehealthcare.eve_healthcare_booking_api.entity.User;
import com.evehealthcare.eve_healthcare_booking_api.repository.BookingRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

        @Mock
        private PaymentRepository paymentRepository;

        @Mock
        private BookingRepository bookingRepository;

        @InjectMocks
        private PaymentService paymentService;

        @Test
        void processPayment_shouldConfirmBooking_whenPaymentSucceeds() {

                User user = new User(
                                "Test User",
                                "testuser@gmail.com",
                                "password");

                Booking booking = new Booking();

                booking.setId(1L);
                booking.setUser(user);
                booking.setAmount(new BigDecimal("500.00"));
                booking.setStatus(BookingStatus.PENDING);

                when(bookingRepository.findById(1L))
                                .thenReturn(Optional.of(booking));

                when(paymentRepository.findByBookingId(1L))
                                .thenReturn(Optional.empty());

                when(paymentRepository.save(any(Payment.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Payment result = paymentService.processPayment(
                                1L,
                                true,
                                "testuser@gmail.com");

                assertEquals(
                                PaymentStatus.SUCCESS,
                                result.getStatus());

                assertEquals(
                                BookingStatus.CONFIRMED,
                                booking.getStatus());

                assertEquals(
                                new BigDecimal("500.00"),
                                result.getAmount());

                verify(bookingRepository).save(booking);
                verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        void processPayment_shouldFailBooking_whenPaymentFails() {

                User user = new User(
                                "Test User",
                                "testuser@gmail.com",
                                "password");

                Booking booking = new Booking();

                booking.setId(2L);
                booking.setUser(user);
                booking.setAmount(new BigDecimal("500.00"));
                booking.setStatus(BookingStatus.PENDING);

                when(bookingRepository.findById(2L))
                                .thenReturn(Optional.of(booking));

                when(paymentRepository.findByBookingId(2L))
                                .thenReturn(Optional.empty());

                when(paymentRepository.save(any(Payment.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Payment result = paymentService.processPayment(
                                2L,
                                false,
                                "testuser@gmail.com");

                assertEquals(
                                PaymentStatus.FAILED,
                                result.getStatus());

                assertEquals(
                                BookingStatus.FAILED,
                                booking.getStatus());

                verify(bookingRepository).save(booking);
                verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        void processPayment_shouldRejectDuplicatePayment() {

                User user = new User(
                                "Test User",
                                "testuser@gmail.com",
                                "password");

                Booking booking = new Booking();

                booking.setId(3L);
                booking.setUser(user);
                booking.setAmount(new BigDecimal("500.00"));
                booking.setStatus(BookingStatus.PENDING);

                Payment existingPayment = new Payment();
                existingPayment.setBooking(booking);
                existingPayment.setStatus(PaymentStatus.SUCCESS);

                when(bookingRepository.findById(3L))
                                .thenReturn(Optional.of(booking));

                when(paymentRepository.findByBookingId(3L))
                                .thenReturn(Optional.of(existingPayment));

                RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                                RuntimeException.class,
                                () -> paymentService.processPayment(
                                                3L,
                                                true,
                                                "testuser@gmail.com"));

                assertEquals(
                                "Payment already exists for this booking",
                                exception.getMessage());

                verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        void processPayment_shouldRejectUnauthorizedUser() {

                User owner = new User(
                                "Booking Owner",
                                "owner@gmail.com",
                                "password");

                Booking booking = new Booking();

                booking.setId(4L);
                booking.setUser(owner);
                booking.setAmount(new BigDecimal("500.00"));
                booking.setStatus(BookingStatus.PENDING);

                when(bookingRepository.findById(4L))
                                .thenReturn(Optional.of(booking));

                RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                                RuntimeException.class,
                                () -> paymentService.processPayment(
                                                4L,
                                                true,
                                                "differentuser@gmail.com"));

                assertEquals(
                                "You are not authorized to access this booking",
                                exception.getMessage());

                verify(paymentRepository, never()).save(any(Payment.class));
                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        void processWebhook_shouldReturnSamePayment_whenEventIsDuplicate() {

                User user = new User(
                                "Test User",
                                "testuser@gmail.com",
                                "password");

                Booking booking = new Booking();

                booking.setId(5L);
                booking.setUser(user);
                booking.setAmount(new BigDecimal("500.00"));
                booking.setStatus(BookingStatus.CONFIRMED);

                Payment existingPayment = new Payment();

                existingPayment.setBooking(booking);
                existingPayment.setStatus(PaymentStatus.SUCCESS);
                existingPayment.setWebhookEventId("evt_test_001");

                PaymentWebhookRequest request = new PaymentWebhookRequest();
                request.setEventId("evt_test_001");
                request.setBookingId(5L);
                request.setStatus(PaymentStatus.SUCCESS);

                when(paymentRepository.findByWebhookEventId("evt_test_001"))
                                .thenReturn(Optional.of(existingPayment));

                Payment result = paymentService.processWebhook(request);

                assertEquals(existingPayment, result);
                assertEquals(
                                PaymentStatus.SUCCESS,
                                result.getStatus());

                assertEquals(
                                "evt_test_001",
                                result.getWebhookEventId());

                verify(paymentRepository, never()).save(any(Payment.class));
                verify(bookingRepository, never()).save(any(Booking.class));
        }
}