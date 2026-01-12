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


   @GetMapping 
          public List<SubscriptionResponse> list(@AuthenticationPrincipal UserDetails userDetails) {      List<Subscription> subscriptions = subscriptionService.listSubscription(userDetails.getUsername()); 
          return subscriptions.stream() 
        .map(SubscriptionResponse::new) 
        .toList();
 }

   @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SubscriptionRequest dto) {
            // Chama o service para criar a subscription com validações e regras de negócio
            Subscription saved = subscriptionService.createSubscription(userDetails.getUsername(), dto);
            // Retorna 201 Created com o DTO de resposta
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SubscriptionResponse(saved));
    }

    
    @PatchMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> partialUpdate(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody SubscriptionUpdateRequest dto) {
        // Chama o service para atualizar parcialmente
        Subscription updated = subscriptionService.partialUpdate(id, userDetails.getUsername(), dto);
        // Retorna 200 OK com o DTO atualizado
        return ResponseEntity.ok(new SubscriptionResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
            subscriptionService.delete(id, userDetails.getUsername());

            return ResponseEntity.noContent().build(); 
    }



    }
    
    





