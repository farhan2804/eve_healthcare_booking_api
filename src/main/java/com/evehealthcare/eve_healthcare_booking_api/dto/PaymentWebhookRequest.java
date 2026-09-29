package com.evehealthcare.eve_healthcare_booking_api.dto;

import com.evehealthcare.eve_healthcare_booking_api.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentWebhookRequest {

    @NotBlank
    private String eventId;

    @NotNull
    private Long bookingId;

    @NotNull
    private PaymentStatus status;

    public PaymentWebhookRequest() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}