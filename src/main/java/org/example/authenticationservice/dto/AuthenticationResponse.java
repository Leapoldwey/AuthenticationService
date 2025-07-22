package org.example.authenticationservice.dto;

public record AuthenticationResponse (String accessToken, String refreshToken, String username) {}

