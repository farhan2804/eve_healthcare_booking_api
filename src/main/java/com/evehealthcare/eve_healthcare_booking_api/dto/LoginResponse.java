package com.evehealthcare.eve_healthcare_booking_api.dto;

public class LoginResponse {
    private String token;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
