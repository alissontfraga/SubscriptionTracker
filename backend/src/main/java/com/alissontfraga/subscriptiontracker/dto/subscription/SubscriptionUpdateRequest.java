package com.alissontfraga.subscriptiontracker.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alissontfraga.subscriptiontracker.enums.Category;
import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Status;

public record SubscriptionUpdateRequest(

        String name,
        BigDecimal price,
        Currency currency,
        Frequency frequency,
        Category category,
        Status status,
        LocalDate startDate,
        LocalDate renewalDate) {
    
}
