package com.evehealthcare.eve_healthcare_booking_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.evehealthcare.eve_healthcare_booking_api.dto.LoginRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.LoginResponse;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupRequest;
import com.evehealthcare.eve_healthcare_booking_api.dto.SignupResponse;
import com.evehealthcare.eve_healthcare_booking_api.repository.UserRepository;
import com.evehealthcare.eve_healthcare_booking_api.util.JwtUtil;
import com.evehealthcare.eve_healthcare_booking_api.entity.User;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return response;
    }

    public SignupResponse signup(SignupRequest request) {
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

        return response;

    }

}
