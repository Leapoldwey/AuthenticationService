package org.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.*;
import org.example.authenticationservice.entity.RefreshToken;
import org.example.authenticationservice.entity.User;
import org.example.authenticationservice.exception.customExceptions.UserAlreadyExistsException;
import org.example.authenticationservice.mapper.UserMapper;
import org.example.authenticationservice.repository.RefreshTokenRepository;
import org.example.authenticationservice.securityConfig.CustomUserDetails;
import org.example.authenticationservice.securityConfig.CustomUserDetailsService;
import org.example.authenticationservice.securityConfig.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthenticationResponse registerUser(SignUpRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.email())) {
            throw new UserAlreadyExistsException(
                    String.format("Email \"%s\" already exists", signUpRequest.email())
            );
        }
        if (userService.existsByLogin(signUpRequest.login())) {
            throw new UserAlreadyExistsException(
                    String.format("Login \"%s\" already exists", signUpRequest.login())
            );
        }

        UserDto userDto = new UserDto();
        userDto.setEmail(signUpRequest.email());
        userDto.setPassword(signUpRequest.password());
        userDto.setLogin(signUpRequest.login());

        userService.save(userDto);

        return new AuthenticationResponse(null, null, userDto.getEmail());
    }

    @Override
    public AuthenticationResponse loginUser(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.email(), signInRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshTokenString = jwtService.generateRefreshToken(userDetails);

        UserDto userDto = userService.findByEmail(userDetails.getUsername());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUser(userMapper.mapTo(userDto));
        refreshToken.setToken(refreshTokenString);
        refreshToken.setRevoked(false);

        Date expirationDate = jwtService.extractExpiration(refreshTokenString);
        refreshToken.setExpiryDate(expirationDate);

        refreshTokenRepository.save(refreshToken);

        return new AuthenticationResponse(
                accessToken,
                refreshTokenString,
                userDetails.getUsername()
        );
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken()).orElseThrow(
                () -> new NoSuchElementException("Refresh token not found")
        );

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().before(new Date())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));

        return new AuthenticationResponse(newAccessToken, refreshTokenRequest.refreshToken(), user.getEmail());
    }

    @Override
    public AuthenticationResponse logoutUser(LogoutRequest logoutRequest) {
        RefreshToken token = refreshTokenRepository.findByToken(logoutRequest.refreshToken()).orElseThrow(
                () -> new NoSuchElementException("Refresh token not found")
        );
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        return new AuthenticationResponse(null, null, null);
    }

}
