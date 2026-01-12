package com.alissontfraga.subscriptiontracker.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alissontfraga.subscriptiontracker.enums.Category;
import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscriptionRequest(

        @NotBlank
        String name,

        @NotNull
        BigDecimal price,

        @NotNull
        Currency currency,

        @NotNull
        Frequency frequency,

        @NotNull
        Category category,

        @NotNull
        Status status,

        LocalDate startDate,
        
        LocalDate renewalDate
) {}
