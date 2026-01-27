package com.alissontfraga.subscriptiontracker.integration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {
    
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected void ensureUserExists(String username) {
        ensureUserExists(username, Set.of(Role.ROLE_USER));
    }

    protected void ensureUserExists(String username, Set<Role> roles) {
        if (!userRepository.existsByUsername(username)) {

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("123"));
            user.setRoles(roles);

            userRepository.save(user);
        }
    }
}
