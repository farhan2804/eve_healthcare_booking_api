package com.evehealthcare.eve_healthcare_booking_api.repository;

import com.evehealthcare.eve_healthcare_booking_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}