package com.evehealthcare.eve_healthcare_booking_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class CentreTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "centre_id", nullable = false)
    private DiagnosticCentre centre;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private DiagnosticTest test;

    @Column(nullable = false)
    private BigDecimal price;

    public CentreTest() {
    }

    public CentreTest(DiagnosticCentre centre, DiagnosticTest test, BigDecimal price) {
        this.centre = centre;
        this.test = test;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiagnosticCentre getCentre() {
        return centre;
    }

    public void setCentre(DiagnosticCentre centre) {
        this.centre = centre;
    }

    public DiagnosticTest getTest() {
        return test;
    }

    public void setTest(DiagnosticTest test) {
        this.test = test;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}