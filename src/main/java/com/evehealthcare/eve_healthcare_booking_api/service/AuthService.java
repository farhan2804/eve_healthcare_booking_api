package com.evehealthcare.eve_healthcare_booking_api.service;

import com.evehealthcare.eve_healthcare_booking_api.dto.LoginRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.LoginResponse;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupResponse;
import com.evehealthcare.eve_healthcare_booking_api.entity.User;
import com.evehealthcare.eve_healthcare_booking_api.repository.UserRepository;
import com.evehealthcare.eve_healthcare_booking_api.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {

        log.info("Login requested for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        log.info("User logged in successfully: {}",
                request.getEmail());

        return response;
    }

    public SignupResponse signup(SignupRequest request) {

        log.info("User signup requested for email: {}",
                request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword);

        User savedUser = userRepository.save(user);

        SignupResponse response = new SignupResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        log.info("User registered successfully: {}",
                user.getEmail());

        return response;
    }
}