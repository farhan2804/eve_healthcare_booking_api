package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticTest;
import com.evehealthcare.eve_healthcare_booking_api.service.DiagnosticTestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class DiagnosticTestController {

    private final DiagnosticTestService testService;

    public DiagnosticTestController(DiagnosticTestService testService) {
        this.testService = testService;
    }

    @PostMapping
    public DiagnosticTest createTest(@RequestBody DiagnosticTest test) {
        return testService.createTest(test);
    }

    @GetMapping
    public List<DiagnosticTest> getAllTests() {
        return testService.getAllTests();
    }
}