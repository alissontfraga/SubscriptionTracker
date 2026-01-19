package com.alissontfraga.subscriptiontracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionRequest;
import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionResponse;
import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionUpdateRequest;
import com.alissontfraga.subscriptiontracker.entity.Subscription;
import com.alissontfraga.subscriptiontracker.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<SubscriptionResponse> list(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<Subscription> subscriptions =
                subscriptionService.listSubscription(userDetails.getUsername());

        return subscriptions.stream()
                .map(SubscriptionResponse::new)
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SubscriptionRequest dto
    ) {
        Subscription saved =
                subscriptionService.createSubscription(userDetails.getUsername(), dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SubscriptionResponse(saved));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> partialUpdate(
    @PathVariable Long id,
    Authentication authentication,
    @RequestBody @Valid SubscriptionUpdateRequest dto) {

        Subscription updated =
        subscriptionService.partialUpdate(id, authentication.getName(), dto);

        return ResponseEntity.ok(new SubscriptionResponse(updated));
    }


    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        subscriptionService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
