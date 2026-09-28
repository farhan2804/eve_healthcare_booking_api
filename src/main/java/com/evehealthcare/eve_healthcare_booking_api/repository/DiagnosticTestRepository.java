package com.evehealthcare.eve_healthcare_booking_api.repository;

import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticTestRepository extends JpaRepository<DiagnosticTest, Long> {
}