package com.alissontfraga.subscriptiontracker.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alissontfraga.subscriptiontracker.entity.Subscription;
import com.alissontfraga.subscriptiontracker.enums.Status;
import com.alissontfraga.subscriptiontracker.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    
    public Subscription createSubscription(Subscription sub) {
        validateStartDate(sub);

        if (sub.getRenewalDate() == null) {
            sub.setRenewalDate(calculateRenewalDate(sub));
        }

        updateDisplayStatus(sub);

        return subscriptionRepository.save(sub);
    }


    public Subscription updateSubscription(Long id, Subscription updated) {
        Subscription existing = subscriptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Subscription not found"));

        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setFrequency(updated.getFrequency());
        existing.setCategory(updated.getCategory());
        existing.setStartDate(updated.getStartDate());
        existing.setRenewalDate(updated.getRenewalDate());

        validateStartDate(existing);

        if (existing.getRenewalDate() == null) {
            existing.setRenewalDate(calculateRenewalDate(existing));
        }

        updateDisplayStatus(existing);

        return subscriptionRepository.save(existing);

    }




    private void validateStartDate(Subscription sub) {
        if (sub.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be in the past");
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
