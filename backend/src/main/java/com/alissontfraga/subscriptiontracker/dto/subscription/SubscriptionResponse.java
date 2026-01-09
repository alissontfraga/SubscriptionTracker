package com.alissontfraga.subscriptiontracker.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alissontfraga.subscriptiontracker.enums.Currency;
import com.alissontfraga.subscriptiontracker.enums.Frequency;
import com.alissontfraga.subscriptiontracker.enums.Status;

public record SubscriptionResponse(
    Long id,

    String name,

    BigDecimal price,

    Currency currency,

    Frequency frequency,

    Status status,

    LocalDate startDate,

    LocalDate renewalDate,

    Long ownerId
) {}
