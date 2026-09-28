package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.entity.CentreTest;
import com.evehealthcare.eve_healthcare_booking_api.service.CentreTestService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/centre-tests")
public class CentreTestController {

    private final CentreTestService centreTestService;

    public CentreTestController(CentreTestService centreTestService) {
        this.centreTestService = centreTestService;
    }

    @PostMapping
    public CentreTest addTestToCentre(
            @RequestParam Long centreId,
            @RequestParam Long testId,
            @RequestParam BigDecimal price) {

        return centreTestService.addTestToCentre(
                centreId,
                testId,
                price);
    }
}