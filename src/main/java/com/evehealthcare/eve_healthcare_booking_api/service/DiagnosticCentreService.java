package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.entity.DiagnosticCentre;
import com.evehealthcare.eve_healthcare_booking_api.repository.DiagnosticCentreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticCentreService {

    private final DiagnosticCentreRepository centreRepository;

    public DiagnosticCentreService(DiagnosticCentreRepository centreRepository) {
        this.centreRepository = centreRepository;
    }

    public DiagnosticCentre createCentre(DiagnosticCentre centre) {
        return centreRepository.save(centre);
    }

    public List<DiagnosticCentre> getAllCentres() {
        return centreRepository.findAll();
    }
}