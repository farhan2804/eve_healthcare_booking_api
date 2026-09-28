package com.evehealthcare.eve_healthcare_booking_api.entity;

import jakarta.persistence.*;

@Entity
public class DiagnosticTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public DiagnosticTest() {
    }

    public DiagnosticTest(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}