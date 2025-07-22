package org.example.authenticationservice.service;

import org.example.authenticationservice.dto.*;

public interface AuthService {
    AuthenticationResponse registerUser(SignUpRequest signUpRequest);
    AuthenticationResponse loginUser(SignInRequest signInRequest);
    AuthenticationResponse logoutUser(LogoutRequest logoutRequest);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
