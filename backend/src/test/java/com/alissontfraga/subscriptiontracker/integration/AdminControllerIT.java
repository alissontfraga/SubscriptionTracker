package com.alissontfraga.subscriptiontracker.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.repository.UserRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateAdminUser() throws Exception {
        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "superadmin",
                        "password": "super123"
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("superadmin"));

        // verifica no banco
        User created = userRepository.findByUsername("superadmin").orElseThrow();
        assertTrue(created.getRoles().contains(Role.ROLE_ADMIN));
    }

    @Test
    @WithMockUser(username = "charlotte", roles = "USER")
    void shouldRejectAdminCreationForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "hacker",
                        "password": "123"
                    }
                """))
            .andExpect(status().isForbidden());
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteUserAsAdmin() throws Exception {

        ensureUserExists("victim");

        mockMvc.perform(delete("/api/admin/users/{username}", "victim"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "liz", roles = "USER")
    void shouldRejectDeleteWhenNotAdmin() throws Exception {

        ensureUserExists("victim");

        mockMvc.perform(delete("/api/admin/users/{username}", "victim"))
            .andExpect(status().isForbidden());
    }

}

