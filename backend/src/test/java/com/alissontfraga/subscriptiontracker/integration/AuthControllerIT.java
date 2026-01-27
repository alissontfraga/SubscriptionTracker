package com.alissontfraga.subscriptiontracker.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alissontfraga.subscriptiontracker.repository.UserRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "alice",
                      "password": "123"
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        ensureUserExists("destiny");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "destiny",
                      "password": "123"
                    }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldRejectNonExistingUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "null",
                      "password": "wrong"
                    }
                """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shoulRejectInvalidPassword() throws Exception {

        ensureUserExists("flower");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("""
              {
                 "username": "flower",
                "password": "wrongpassword"
              }
         """))
        .andExpect(status().isUnauthorized());
    }

}
