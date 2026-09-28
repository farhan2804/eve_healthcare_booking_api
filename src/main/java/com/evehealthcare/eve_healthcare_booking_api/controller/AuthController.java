package com.evehealthcare.eve_healthcare_booking_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evehealthcare.eve_healthcare_booking_api.dto.SignupRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupResponse;
import com.evehealthcare.eve_healthcare_booking_api.service.AuthService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    // injecting authservice
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

}
