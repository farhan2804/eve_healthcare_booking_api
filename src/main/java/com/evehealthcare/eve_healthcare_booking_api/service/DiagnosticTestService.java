package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticTest;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticTestService {

    private final DiagnosticTestRepository testRepository;

    public DiagnosticTestService(DiagnosticTestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public DiagnosticTest createTest(DiagnosticTest test) {
        return testRepository.save(test);
    }

    public List<DiagnosticTest> getAllTests() {
        return testRepository.findAll();
    }
}