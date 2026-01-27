package com.alissontfraga.subscriptiontracker.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alissontfraga.subscriptiontracker.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionControllerIT extends AbstractIntegrationTest{
    
     @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    
    @Nested
    class listSubscription {
    @Test
    @WithMockUser(username = "alice", roles = "USER")
    void shouldListUserSubscriptions() throws Exception {

        ensureUserExists("alice");

        mockMvc.perform(get("/api/subscriptions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
         }
    }


    @Test
    @WithMockUser(username = "lice", roles = "USER")
    void shouldCreateSubscription() throws Exception {

        ensureUserExists("lice");

        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Netflix",
                      "price": "39.90",
                      "currency": "BRL",
                      "frequency": "MONTHLY",
                      "category": "SERVICE",
                      "status": "ACTIVE",
                      "startDate": "2025-01-01",
                      "renewalDate": "2025-02-01"
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }


    @Test
    @WithMockUser(username = "charlie", roles = "USER")
    void shouldPartiallyUpdateOwnSubscription() throws Exception {

        ensureUserExists("charlie");

        // create
        MvcResult result =
            mockMvc.perform(post("/api/subscriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Netflix",
                          "price": 39.90,
                          "currency": "BRL",
                          "frequency": "MONTHLY",
                          "category": "SERVICE",
                          "status": "ACTIVE",
                          "startDate": "2025-01-01",
                          "renewalDate": "2025-02-01"
                        }
                    """))
                .andExpect(status().isCreated())
                .andReturn();

        Number idNumber = JsonPath.read(
            result.getResponse().getContentAsString(),
            "$.id"
        );
        Long id = idNumber.longValue();

        // patch
        mockMvc.perform(patch("/api/subscriptions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "price": 49.90
                    }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(49.90));
    }


    @Test
    @WithMockUser(username = "ashley", roles = "USER")
    void shouldDeleteOwnSubscription() throws Exception {

        ensureUserExists("ashley");

        MvcResult result =
            mockMvc.perform(post("/api/subscriptions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Netflix",
                          "price": "39.90",
                          "currency": "BRL",
                          "frequency": "MONTHLY",
                          "category": "SERVICE",
                          "status": "ACTIVE",
                          "startDate": "2025-01-01",
                          "renewalDate": "2025-02-01"
                        }
                    """))
                .andExpect(status().isCreated())
                .andReturn();

     //id
        Number idNumber = JsonPath.read(
            result.getResponse().getContentAsString(),
            "$.id"
        );
        Long id = idNumber.longValue();
    //id

        mockMvc.perform(delete("/api/subscriptions/{id}", id))
            .andExpect(status().isNoContent());
    }



}
