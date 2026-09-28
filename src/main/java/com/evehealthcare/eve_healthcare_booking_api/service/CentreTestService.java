package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.entity.CentreTest;
import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticCentre;
import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticTest;
import com.evehealthcare.eve_healthcare_booking_api.repository.CentreTestRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticCentreRepository;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticTestRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CentreTestService {

    private final CentreTestRepository centreTestRepository;
    private final DiagnosticCentreRepository centreRepository;
    private final DiagnosticTestRepository testRepository;

    public CentreTestService(
            CentreTestRepository centreTestRepository,
            DiagnosticCentreRepository centreRepository,
            DiagnosticTestRepository testRepository) {

        this.centreTestRepository = centreTestRepository;
        this.centreRepository = centreRepository;
        this.testRepository = testRepository;
    }

    public CentreTest addTestToCentre(
            Long centreId,
            Long testId,
            BigDecimal price) {

        DiagnosticCentre centre = centreRepository.findById(centreId)
                .orElseThrow(() -> new RuntimeException("Diagnostic centre not found"));

        DiagnosticTest test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Diagnostic test not found"));

        CentreTest centreTest = new CentreTest(centre, test, price);

        return centreTestRepository.save(centreTest);
    }
}