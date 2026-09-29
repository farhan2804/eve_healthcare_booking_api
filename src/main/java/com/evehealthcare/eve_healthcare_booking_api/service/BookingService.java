package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.dto.BookingRequest;
import com.evehealthcare.eve_healthcare_booking_api.entity.*;
import com.evehealthcare.eve_healthcare_booking_api.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DiagnosticCentreRepository centreRepository;
    private final DiagnosticTestRepository testRepository;
    private final CentreTestRepository centreTestRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            DiagnosticCentreRepository centreRepository,
            DiagnosticTestRepository testRepository,
            CentreTestRepository centreTestRepository) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.centreRepository = centreRepository;
        this.testRepository = testRepository;
        this.centreTestRepository = centreTestRepository;
    }

    public Booking createBooking(
            BookingRequest request,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DiagnosticCentre centre = centreRepository.findById(request.getCentreId())
                .orElseThrow(() -> new RuntimeException("Diagnostic centre not found"));

        DiagnosticTest test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new RuntimeException("Diagnostic test not found"));

        CentreTest centreTest = centreTestRepository.findAll()
                .stream()
                .filter(ct -> ct.getCentre().getId().equals(centre.getId())
                        && ct.getTest().getId().equals(test.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "This test is not available at the selected centre"));

        BigDecimal price = centreTest.getPrice();

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setDiagnosticCentre(centre);
        booking.setDiagnosticTest(test);
        booking.setAppointmentDateTime(request.getAppointmentDateTime());
        booking.setAmount(price);
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }
}