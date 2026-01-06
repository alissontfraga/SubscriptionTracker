package com.alissontfraga.subscriptiontracker.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private Long jwtExpirationMs;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }


    public String generateToken(String username) {
        return JWT.create()
        .withSubject(username)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .sign(getAlgorithm());
    }



    public String getUsernameFromToken(String token) {
        return verifyToken(token).getSubject();
    }


    public boolean validateToken(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JWTVerificationException ex) {
            log.error("JWT inválido: " + ex.getMessage()); //log serve pra debug, mostra no console
            return false;
        }
    }


    private DecodedJWT verifyToken(String token) {
        return JWT.require(getAlgorithm()).build().verify(token);
    }

}
