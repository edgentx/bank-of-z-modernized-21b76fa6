package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Validation in constructor for safety
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}