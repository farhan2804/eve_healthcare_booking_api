package com.evehealthcare.eve_healthcare_booking_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private DiagnosticTest diagnosticTest;

    @ManyToOne
    @JoinColumn(name = "centre_id", nullable = false)
    private DiagnosticCentre diagnosticCentre;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DiagnosticTest getDiagnosticTest() {
        return diagnosticTest;
    }

    public void setDiagnosticTest(DiagnosticTest diagnosticTest) {
        this.diagnosticTest = diagnosticTest;
    }

    public DiagnosticCentre getDiagnosticCentre() {
        return diagnosticCentre;
    }

    public void setDiagnosticCentre(DiagnosticCentre diagnosticCentre) {
        this.diagnosticCentre = diagnosticCentre;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}