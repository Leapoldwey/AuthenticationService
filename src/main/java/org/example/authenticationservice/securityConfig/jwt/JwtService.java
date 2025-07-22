package org.example.authenticationservice.securityConfig.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    @Value("${token.secret}")
    private String secret;
    @Value("${token.validityTimeAccess}")
    private Long validityTimeAccess;
    @Value("${token.validityTimeRefresh}")
    private Long validityTimeRefresh;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validityTimeAccess))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validityTimeRefresh))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }
}
