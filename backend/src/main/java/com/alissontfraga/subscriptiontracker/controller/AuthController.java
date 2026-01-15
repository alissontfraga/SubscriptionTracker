package com.alissontfraga.subscriptiontracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.subscriptiontracker.dto.auth.AuthRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.AuthResponse;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterResponse;
import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.security.JwtUtil;
import com.alissontfraga.subscriptiontracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest dto ) {
        User user = userService.createUser(dto.username(), dto.password());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new RegisterResponse(user.getId(), user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        User user = userService.findByUsername(req.username());

        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }
}
