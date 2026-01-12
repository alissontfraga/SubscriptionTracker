package com.alissontfraga.subscriptiontracker.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alissontfraga.subscriptiontracker.entity.Subscription;
import com.alissontfraga.subscriptiontracker.enums.Category;
import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Status;

public record SubscriptionResponse(
        Long id,
        String name,
        BigDecimal price,
        Currency currency,
        Frequency frequency,
        Category category,
        Status status,
        LocalDate startDate,
        LocalDate renewalDate
) {
    public SubscriptionResponse(Subscription subscription) {
        this(
            subscription.getId(),
            subscription.getName(),
            subscription.getPrice(),
            subscription.getCurrency(),
            subscription.getFrequency(),
            subscription.getCategory(),
            subscription.getStatus(),
            subscription.getStartDate(),
            subscription.getRenewalDate()
        );
    }
}