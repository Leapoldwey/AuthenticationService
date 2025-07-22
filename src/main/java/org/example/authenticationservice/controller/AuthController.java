package org.example.authenticationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.*;
import org.example.authenticationservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        AuthenticationResponse newUser = authService.registerUser(signUpRequest);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody SignInRequest signInRequest) {
        AuthenticationResponse login = authService.loginUser(signInRequest);

        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthenticationResponse authenticationResponse = authService.refreshToken(refreshTokenRequest);

        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logoutUser(logoutRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
