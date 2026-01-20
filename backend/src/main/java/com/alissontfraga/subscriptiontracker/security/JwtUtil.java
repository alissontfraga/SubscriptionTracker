package com.alissontfraga.subscriptiontracker.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    private static final String ROLES_CLAIM = "roles";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private Long expiration;

    public String generateToken(User user) {
        Instant now = Instant.now();

        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim(
                ROLES_CLAIM,
                user.getRoles().stream().map(Enum::name).toList()
            )
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusMillis(expiration)))
            .sign(algorithm());
    }

    public String getUsername(String token) {
        return verify(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            log.warn("JWT inválido ou expirado: {}", ex.getMessage());
            return false;
        }
    }

    /* =======================
       Métodos internos
       ======================= */

    private DecodedJWT verify(String token) {
        return JWT.require(algorithm()).build().verify(token);
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }
}
