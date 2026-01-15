package com.alissontfraga.subscriptiontracker.security;

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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private Long expiration;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateToken(User user) {
        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("roles",
                user.getRoles().stream().map(Enum::name).toList()
            )
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
            .sign(algorithm());
    }

    public String getUsername(String token) {
        return verify(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("JWT inválido");
            return false;
        }
    }

    private DecodedJWT verify(String token) {
        return JWT.require(algorithm()).build().verify(token);
    }
}
