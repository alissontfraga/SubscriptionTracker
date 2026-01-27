package com.alissontfraga.subscriptiontracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.repository.UserRepository;
import com.alissontfraga.subscriptiontracker.service.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
 class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        String username = "alisso";
        String password = "$bcrypt";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.getRoles().add(Role.ROLE_USER);

        when(userRepository.findByUsername(username))
            .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(
            userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        String username = "unknown";

        when(userRepository.findByUsername(username))
            .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(username));
    }
}

