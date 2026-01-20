package com.alissontfraga.subscriptiontracker.integration.flow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.repository.UserRepository;

@AutoConfigureMockMvc
class ListSubscriptionsFlowIT extends AbstractIntegrationTest{

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        ensureUserExists("alice");
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldListUserSubscriptions() throws Exception {

        mockMvc.perform(get("/api/subscriptions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    private void ensureUserExists(String username) {
    if (!userRepository.existsByUsername(username)) {

        User user = new User();
        user.setUsername(username);
        user.setPassword("123"); // bcrypt se necessário

        // ajuste conforme seu modelo
        user.setRoles(Set.of(Role.ROLE_USER));

        userRepository.save(user);
    }
}
}
