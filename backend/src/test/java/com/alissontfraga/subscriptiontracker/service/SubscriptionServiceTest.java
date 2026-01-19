package com.alissontfraga.subscriptiontracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionRequest;
import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionUpdateRequest;
import com.alissontfraga.subscriptiontracker.entity.Subscription;
import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Category;
import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Role;
import com.alissontfraga.subscriptiontracker.enums.Status;
import com.alissontfraga.subscriptiontracker.exception.BadRequestException;
import com.alissontfraga.subscriptiontracker.exception.ForbiddenException;
import com.alissontfraga.subscriptiontracker.exception.ResourceNotFoundException;
import com.alissontfraga.subscriptiontracker.repository.SubscriptionRepository;

// AAA - Arrange - Act - Assert

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserService userService;


    // Helpers 

    private User user(Long id, String username) {
        return new User(
            id,
            username,
            "password",
            Set.of(Role.ROLE_USER)
        );
    }

    private Subscription subscription(User owner) {
        Subscription s = new Subscription();
        s.setId(1L);
        s.setOwner(owner);
        s.setName("Netflix");
        s.setStartDate(LocalDate.now().minusDays(1));
        s.setRenewalDate(LocalDate.now().plusDays(1));
        s.setFrequency(Frequency.MONTHLY);
        s.setStatus(Status.ACTIVE);
        return s;
    }



    @Nested
    class listSubscription {

        @DisplayName("Should return a List of Subscriptions when User has Subscriptions")
        @Test
        void shouldReturnAListOfSubscriptionsWhenUserHasSubscriptions() {
            // ARRANGE
            User user = user(1L, "alisso");
            List<Subscription> subscriptions =
                List.of(new Subscription(), new Subscription());

            when(userService.findByUsername(user.getUsername()))
                .thenReturn(user);

            when(subscriptionRepository.findByOwner_Id(user.getId()))
                .thenReturn(subscriptions);

            // ACT
            List<Subscription> result =
                subscriptionService.listSubscription(user.getUsername());

            // ASSERT
            assertNotNull(result);
            assertEquals(subscriptions, result);
            verify(subscriptionRepository).findByOwner_Id(user.getId());
        }

        @DisplayName("Should return empty List when User has no Subscriptions")
        @Test
        void shouldReturnEmptyWhenUserHasNoSubscriptions() {
            // ARRANGE
            User user = user(1L, "alisso");

            when(userService.findByUsername(user.getUsername()))
                .thenReturn(user);

            when(subscriptionRepository.findByOwner_Id(user.getId()))
                .thenReturn(List.of());

            // ACT
            List<Subscription> result =
                subscriptionService.listSubscription(user.getUsername());

            // ASSERT
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }



    @Nested
    class createSubscription {

        @DisplayName("Should create Subscription with default dates")
        @Test
        void shouldCreateSubscriptionWithDefaultDates() {
            // ARRANGE
            User user = user(1L, "alisso");

            SubscriptionRequest dto =
                new SubscriptionRequest(
                    "Spotify",
                    BigDecimal.valueOf(19.90),
                    Currency.BRL,
                    Frequency.MONTHLY,
                    Category.ENTERTAINMENT,
                    Status.ACTIVE,
                    null,
                    null
                );

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            when(subscriptionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
//                 inv returns the same object that was passed into the method. 
//                                  You give → subscription  
//                                  You get back → subscription 

            // ACT
            Subscription result =
                subscriptionService.createSubscription("alisso", dto);

            // ASSERT
            assertNotNull(result.getStartDate());
            assertNotNull(result.getRenewalDate());
            assertEquals(user, result.getOwner());
        }

        @DisplayName("Should throw BadRequestException when startDate is more than 1 year in future")
        @Test
        void shouldThrowWhenStartDateIsMoreThanOneYearInFuture() {
            // ARRANGE
            User user = user(1L, "alisso");

            SubscriptionRequest dto =
                new SubscriptionRequest(
                    "Spotify",
                    BigDecimal.TEN,
                    Currency.BRL,
                    Frequency.MONTHLY,
                    Category.ENTERTAINMENT,
                    Status.ACTIVE,
                    LocalDate.now().plusYears(2),
                    null
                );

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            // ACT + ASSERT
            assertThrows(
                BadRequestException.class,
                () -> subscriptionService.createSubscription("alisso", dto)
            );
        }

        @DisplayName("Should throw BadRequestException when renewalDate is before startDate")
        @Test
        void shouldThrowWhenRenewalDateIsBeforeStartDate() {
            // ARRANGE
            User user = user(1L, "alisso");

            SubscriptionRequest dto =
                new SubscriptionRequest(
                    "Spotify",
                    BigDecimal.TEN,
                    Currency.BRL,
                    Frequency.MONTHLY,
                    Category.ENTERTAINMENT,
                    Status.ACTIVE,
                    LocalDate.now(),
                    LocalDate.now().minusDays(1)
                );

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            // ACT + ASSERT
            assertThrows(
                BadRequestException.class,
                () -> subscriptionService.createSubscription("alisso", dto)
            );
        }
    }



    @Nested
    class partialUpdate {

        @DisplayName("Should update Subscription when User is the Owner")
        @Test
        void shouldUpdateSubscriptionWhenUserIsOwner() {
            // ARRANGE
            User user = user(1L, "alisso");
            Subscription subscription = subscription(user);

            SubscriptionUpdateRequest dto =
                new SubscriptionUpdateRequest(
                    "Netflix Premium",
                    BigDecimal.valueOf(39.90),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

            when(subscriptionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));


            // ACT
            Subscription result =
                subscriptionService.partialUpdate(1L, "alisso", dto);

            // ASSERT
            assertEquals("Netflix Premium", result.getName());
            assertEquals(BigDecimal.valueOf(39.90), result.getPrice());
        }

        @DisplayName("Should throw ForbiddenException when User is not the Owner")
        @Test
        void shouldThrowForbiddenWhenUserIsNotOwner() {
            // ARRANGE
            User loggedUser = user(1L, "alisso");
            User owner = user(2L, "other");

            Subscription subscription = subscription(owner);

            SubscriptionUpdateRequest dto =
                new SubscriptionUpdateRequest(null, null, null, null, null, null, null, null);

            when(userService.findByUsername("alisso"))
                .thenReturn(loggedUser);

            when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

            // ACT + ASSERT
            assertThrows(
                ForbiddenException.class,
                () -> subscriptionService.partialUpdate(1L, "alisso", dto)
            );
        }

        @DisplayName("Should throw ResourceNotFoundException when Subscription does not exist")
        @Test
        void shouldThrowWhenSubscriptionDoesNotExist() {
            // ARRANGE
            User user = user(1L, "alisso");

            SubscriptionUpdateRequest dto =
                new SubscriptionUpdateRequest(null, null, null, null, null, null, null, null);

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.empty());

            // ACT + ASSERT
            assertThrows(
                ResourceNotFoundException.class,
                () -> subscriptionService.partialUpdate(1L, "alisso", dto)
            );
        }
    }



    @Nested
    class deleteSubscription {

        @DisplayName("Should delete Subscription when User is the Owner")
        @Test
        void shouldDeleteSubscriptionWhenUserIsOwner() {
            // ARRANGE
            User user = user(1L, "alisso");
            Subscription subscription = subscription(user);

            when(userService.findByUsername("alisso"))
                .thenReturn(user);

            when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

            // ACT
            subscriptionService.delete(1L, "alisso");

            // ASSERT
            verify(subscriptionRepository).delete(subscription);
        }

        @DisplayName("Should throw AccessDeniedException when User is not the Owner")
        @Test
        void shouldThrowAccessDeniedWhenUserIsNotOwner() {
            // ARRANGE
            User loggedUser = user(1L, "alisso");
            User owner = user(2L, "other");

            Subscription subscription = subscription(owner);

            when(userService.findByUsername("alisso"))
                .thenReturn(loggedUser);

            when(subscriptionRepository.findById(1L))
                .thenReturn(Optional.of(subscription));

            // ACT + ASSERT
            assertThrows(
                AccessDeniedException.class,
                () -> subscriptionService.delete(1L, "alisso")
            );
        }
    }
}
