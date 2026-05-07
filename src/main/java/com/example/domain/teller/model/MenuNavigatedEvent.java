package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenuId,
        String action,
        String previousMenuId,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Validation if necessary
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
