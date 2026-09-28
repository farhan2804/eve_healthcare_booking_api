package com.evehealthcare.eve_healthcare_booking_api.controller;

import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticCentre;
import com.evehealthcare.eve_healthcare_booking_api.service.DiagnosticCentreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
public class DiagnosticCentreController {

    private final DiagnosticCentreService centreService;

    public DiagnosticCentreController(DiagnosticCentreService centreService) {
        this.centreService = centreService;
    }

    @PostMapping
    public DiagnosticCentre createCentre(@RequestBody DiagnosticCentre centre) {
        return centreService.createCentre(centre);
    }

    @GetMapping
    public List<DiagnosticCentre> getAllCentres() {
        return centreService.getAllCentres();
    }
}