package com.ciprian.hospital_appointments.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.string}")
    private String JWT_SECRET;

    @Value("${jwt.expiration.time}")
    private Long EXPIRATION_TIME;

    private SecretKey secretKey;

    //aici construim si setam cheia
    @PostConstruct
    private void init() {
        byte[] keybytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keybytes, "HmacSHA256");
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExipred(token));
    }

    private boolean isTokenExipred(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
