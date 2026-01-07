package com.alissontfraga.subscriptiontracker.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.subscriptiontracker.dto.auth.AuthRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.AuthResponse;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterResponse;
import com.alissontfraga.subscriptiontracker.security.JwtUtil;
import com.alissontfraga.subscriptiontracker.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest dto) {
        if (userService.findByUsername(dto.username())!= null)  {
            return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("error", "Username already exists"));
        }
        
      var user = userService.createUser(dto.username(), dto.password(), encoder);

      var response = new RegisterResponse(user.getId(), user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        var user = userService.findByUsername(req.username());
        if (user == null || !encoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(false) // false local host, true em prod (HTTPS)
            .path("/")
            .maxAge(3600)
            .sameSite("Strict")
            .build();
        
        return ResponseEntity.ok()
        .header("Set-Cookie", cookie.toString())
        .build();

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();
        
        return ResponseEntity.ok()
        .header("Set-Cookie", cookie.toString())
        .body(Map.of("message", "Logged out"));
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.getUsernameFromToken(token);
        var user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(Map.of("username", user.getUsername(), "id", user.getId()));

    }
    





}
