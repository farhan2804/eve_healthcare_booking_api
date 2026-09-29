package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.dto.PaymentWebhookRequest;
import com.evehealthcare.eve_healthcare_booking_api.entity.Booking;
import com.evehealthcare.eve_healthcare_booking_api.entity.BookingStatus;
import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticCentre;
import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticTest;
import com.evehealthcare.eve_healthcare_booking_api.entity.User;
import com.evehealthcare.eve_healthcare_booking_api.repository.BookingRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticCentreRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticTestRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.PaymentRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

   private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiagnosticCentreRepository centreRepository;

    @Autowired
    private DiagnosticTestRepository testRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Booking booking;

    @BeforeEach
    void setUp() {

        User user = new User(
                "Integration Test User",
                "integration_" + System.currentTimeMillis() + "@test.com",
                passwordEncoder.encode("password123")
        );

        user = userRepository.save(user);

        DiagnosticCentre centre = new DiagnosticCentre(
                "Integration Test Centre",
                "Test Location"
        );

        centre = centreRepository.save(centre);

        DiagnosticTest test = new DiagnosticTest(
                "Integration Test CBC"
        );

        test = testRepository.save(test);

        booking = new Booking();

        booking.setUser(user);
        booking.setDiagnosticCentre(centre);
        booking.setDiagnosticTest(test);
        booking.setAppointmentDateTime(
                LocalDateTime.now().plusDays(1));
        booking.setAmount(BigDecimal.valueOf(500));
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);
    }

    @AfterEach
    void tearDown() {

        paymentRepository.findByBookingId(booking.getId())
                .ifPresent(paymentRepository::delete);

        bookingRepository.delete(booking);

        centreRepository.delete(booking.getDiagnosticCentre());
        testRepository.delete(booking.getDiagnosticTest());
        userRepository.delete(booking.getUser());
    }

    @Test
    void webhook_shouldConfirmBooking_whenPaymentSucceeds()
            throws Exception {

        PaymentWebhookRequest request =
                new PaymentWebhookRequest();

        request.setEventId(
                "integration-success-" + System.currentTimeMillis());
        request.setBookingId(booking.getId());
        request.setStatus(
                com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus.SUCCESS);

        mockMvc.perform(
                        post("/api/payments/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @Test
    void webhook_shouldBeIdempotent_whenSameEventIsSentTwice()
            throws Exception {

        String eventId =
                "integration-duplicate-" + System.currentTimeMillis();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest();

        request.setEventId(eventId);
        request.setBookingId(booking.getId());
        request.setStatus(
                com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus.SUCCESS);

        mockMvc.perform(
                        post("/api/payments/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/payments/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @Test
    void webhook_shouldReturnBadRequest_whenBookingDoesNotExist()
            throws Exception {

        PaymentWebhookRequest request =
                new PaymentWebhookRequest();

        request.setEventId(
                "integration-invalid-" + System.currentTimeMillis());
        request.setBookingId(999999L);
        request.setStatus(
                com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus.SUCCESS);

        mockMvc.perform(
                        post("/api/payments/webhook")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}