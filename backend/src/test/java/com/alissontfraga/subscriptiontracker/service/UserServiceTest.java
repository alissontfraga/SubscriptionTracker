package com.alissontfraga.subscriptiontracker.service;
// AAA - Arrange - Act - Assert

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Nested
    class createUser {
    /* Teste:	                                            O que valida:
    shouldCreateUserSuccessfully	                         Fluxo feliz
    shouldThrowExceptionWhenUsernameAlreadyExists	         Regra de negócio
    shouldEncodePasswordBeforeSaving	                     Segurança
    */

        @Test
        void shouldCreateUserSuccessfully() {
            // given
            String rawPassword = "123";
            String encodedPassword = "$bcrypt";
            String username = "alisso";

            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            User user = userService.createUser(username, rawPassword);

            // then
            assertNotNull(user);
            assertEquals(username, user.getUsername());
            assertEquals(encodedPassword, user.getPassword());
            assertTrue(user.getRoles().contains(Role.ROLE_USER));
        }


        @Test
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            when(userRepository.existsByUsername("alisson")).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("alisson", "123"));

            verify(userRepository, never()).save(any());
        }


        @Test
        void shouldEncodePasswordBeforeSaving() {
            when(userRepository.existsByUsername("alice")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            User user = userService.createUser("alice", "123");

            assertEquals("encoded", user.getPassword());
            verify(passwordEncoder).encode("123");
        }
    }

    @Nested
    class loadUserByUsername {

        @Test
        void shouldLoadUserByUsernameSuccessfully() {
            // given
            String username = "alisso";
            String password = "$bcrypt";

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.getRoles().add(Role.ROLE_USER);

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

            // when
            UserDetails userDetails = userService.loadUserByUsername(username);

            // then
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
            // given
            String username = "alisso";

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

            // when / then
            UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username)
            );

            assertEquals("User not found", exception.getMessage());

            verify(userRepository).findByUsername(username);
        }

    }

    @Nested
    class findByUsername {

        @Test
        void shouldReturnUserWhenUsernameExists() {
            // given
            String username = "alisso";

            User user = new User();
            user.setUsername(username);

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

            // when
            User result = userService.findByUsername(username);

            // then
            assertNotNull(result);
            assertEquals(username, result.getUsername());
        }

       @Test
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            String username = "alisso";

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

            // when / then
            UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findByUsername(username)
            );

            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    class deleteByUsername {

        @Test
        void shouldDeleteUserWhenUsernameExists() {
            // given
            String username = "alisso";

            User user = new User();
            user.setUsername(username);

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

            // when
            userService.deleteByUsername(username);

            // then
            verify(userRepository).delete(user);
        }


        @Test
        void shouldDoNothingWhenUsernameDoesNotExist() {
            // given
            String username = "alisso";

            when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

            // when
            userService.deleteByUsername(username);

            // then
            verify(userRepository, never()).delete(any(User.class));
        }


    }
}
