package com.alissontfraga.subscriptiontracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionRequest;
import com.alissontfraga.subscriptiontracker.dto.subscription.SubscriptionUpdateRequest;
import com.alissontfraga.subscriptiontracker.entity.Subscription;
import com.alissontfraga.subscriptiontracker.entity.User;
import com.alissontfraga.subscriptiontracker.enums.Status;
import com.alissontfraga.subscriptiontracker.exception.BadRequestException;
import com.alissontfraga.subscriptiontracker.exception.ForbiddenException;
import com.alissontfraga.subscriptiontracker.exception.ResourceNotFoundException;
import com.alissontfraga.subscriptiontracker.repository.SubscriptionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    
    public List<Subscription> listSubscription(String username) {
        User user = userService.findByUsername(username);
        return subscriptionRepository.findByOwner_Id(user.getId());
        // retorna lista vazia se não houver assinaturas → 200 OK no controller
    }


    public Subscription createSubscription(String username, SubscriptionRequest dto) {
    
        User user = userService.findByUsername(username);

        Subscription sub = new Subscription();
        sub.setName(dto.name());
        sub.setPrice(dto.price());
        sub.setCurrency(dto.currency());
        sub.setFrequency(dto.frequency());
        sub.setCategory(dto.category());
        sub.setStatus(dto.status());
        sub.setOwner(user);

        if (dto.startDate() != null) {
            if (dto.startDate().isAfter(LocalDate.now().plusYears(1))) {
                throw new BadRequestException("Start date cannot be more than 1 year in the future");
            }
            sub.setStartDate(dto.startDate());
        } else {
            sub.setStartDate(LocalDate.now()); // default
        }

        if (dto.renewalDate() != null) {
            if (dto.renewalDate().isBefore(sub.getStartDate())) {
                throw new BadRequestException("Renewal date cannot be before start date");
            }
            sub.setRenewalDate(dto.renewalDate());
        } else {
            sub.setRenewalDate(calculateRenewalDate(sub));
        }

        updateDisplayStatus(sub);

        return subscriptionRepository.save(sub);
    }



    public Subscription partialUpdate(Long id, String username, SubscriptionUpdateRequest dto) {
    User user = userService.findByUsername(username);

    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (!Objects.equals(subscription.getOwner().getId(), user.getId())) {
            throw new ForbiddenException("You can't change this subscription");
            }

        // Atualização parcial: só altera se o campo vier no DTO
        if (dto.name() != null) subscription.setName(dto.name());
        if (dto.price() != null) subscription.setPrice(dto.price());
        if (dto.currency() != null) subscription.setCurrency(dto.currency());
        if (dto.frequency() != null) subscription.setFrequency(dto.frequency());
        if (dto.category() != null) subscription.setCategory(dto.category());
        if (dto.status() != null) subscription.setStatus(dto.status());

        if (dto.startDate() != null) {
            validateStartDate(dto.startDate()); // valida startDate
            subscription.setStartDate(dto.startDate());
        }

        if (dto.renewalDate() != null) {
            validateRenewalDate(dto.renewalDate(), subscription.getStartDate()); // valida renewalDate
            subscription.setRenewalDate(dto.renewalDate());
        }

        // Atualiza status de exibição se tiver lógica específica
        updateDisplayStatus(subscription);

         return subscriptionRepository.save(subscription);
    }



     public void delete(Long id, String username) {

        User user = userService.findByUsername(username);

        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));

        if (!subscription.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can't delete this subscription");
        }

        subscriptionRepository.delete(subscription);
    }


    //secondaries

    private void validateStartDate(LocalDate startDate) {
    if (startDate.isAfter(LocalDate.now())) {
        throw new BadRequestException("Start date must be in the past");
        }
    }

    private void validateRenewalDate(LocalDate renewalDate, LocalDate startDate) {
    if (renewalDate.isBefore(startDate)) {
        throw new BadRequestException("Renewal date cannot be before start date");
        }
    }

    private LocalDate calculateRenewalDate(Subscription sub) {
    return switch (sub.getFrequency()) {
        case DAILY -> sub.getStartDate().plusDays(1);
        case WEEKLY -> sub.getStartDate().plusWeeks(1);
        case MONTHLY -> sub.getStartDate().plusMonths(1);
        case YEARLY -> sub.getStartDate().plusYears(1);
        };
    }

    private void updateDisplayStatus(Subscription sub) {
        if (sub.getRenewalDate().isBefore(LocalDate.now())) {
            sub.setStatus(Status.EXPIRED);
        }
    }


}
