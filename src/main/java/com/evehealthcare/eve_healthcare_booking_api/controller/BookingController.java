package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.dto.BookingRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.BookingResponse;
import com.evehealthcare.eve_healthcare_booking_api.entity.Booking;
import com.evehealthcare.eve_healthcare_booking_api.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Booking booking = bookingService.createBooking(request, userEmail);

        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setCentreId(booking.getDiagnosticCentre().getId());
        response.setTestId(booking.getDiagnosticTest().getId());
        response.setAppointmentDateTime(booking.getAppointmentDateTime());
        response.setAmount(booking.getAmount());
        response.setStatus(booking.getStatus());

        return response;
    }
}