package com.alissontfraga.subscriptiontracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alissontfraga.subscriptiontracker.controller.AuthController;
import com.alissontfraga.subscriptiontracker.dto.auth.AuthRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.AuthResponse;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterRequest;
import com.alissontfraga.subscriptiontracker.dto.auth.RegisterResponse;
import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.security.JwtUtil;
import com.alissontfraga.subscriptiontracker.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturn401IfUserNotFound() {
        AuthRequest req = new AuthRequest("ghost", "any");

        when(userService.findByUsername("ghost")).thenReturn(null);

        ResponseEntity<AuthResponse> response = authController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldReturn401IfPasswordIncorrect() {
        AuthRequest req = new AuthRequest("flower", "wrongpassword");
        User user = new User();
        user.setUsername("flower");
        user.setPassword("encodedPassword");

        when(userService.findByUsername("flower")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        ResponseEntity<AuthResponse> response = authController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldReturn200AndTokenIfLoginCorrect() {
        AuthRequest req = new AuthRequest("flower", "123");
        User user = new User();
        user.setUsername("flower");
        user.setPassword("encodedPassword");

        when(userService.findByUsername("flower")).thenReturn(user);
        when(passwordEncoder.matches("123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mockedToken");

        ResponseEntity<AuthResponse> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("flower", response.getBody().username());
        assertEquals("mockedToken", response.getBody().token());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest req = new RegisterRequest("alice", "123");
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        when(userService.createUser("alice", "123")).thenReturn(user);

        ResponseEntity<RegisterResponse> response = authController.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("alice", response.getBody().username());
        assertEquals(1L, response.getBody().id());
    }
}

