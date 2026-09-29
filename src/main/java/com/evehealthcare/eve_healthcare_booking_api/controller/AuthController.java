package com.evehealthcare.eve_healthcare_booking_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evehealthcare.eve_healthcare_booking_api.dto.LoginRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.LoginResponse;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupResponse;
import com.evehealthcare.eve_healthcare_booking_api.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    // injecting authservice
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
