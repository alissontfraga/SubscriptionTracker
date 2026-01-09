package com.alissontfraga.subscriptiontracker.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Status;

import jakarta.validation.constraints.NotNull;

public record SubscriptionRequest(

    @NotNull
    String name,
    @NotNull
    BigDecimal price,
    @NotNull
    Currency currency,
    @NotNull
    Frequency frequency,
    @NotNull
    Status status,
    @NotNull
    LocalDate startDate,
    @NotNull
    LocalDate renewalDate
    
) {
    
}
