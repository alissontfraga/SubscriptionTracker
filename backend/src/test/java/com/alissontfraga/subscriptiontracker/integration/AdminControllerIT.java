package com.alissontfraga.subscriptiontracker.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
class AdminControllerIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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

