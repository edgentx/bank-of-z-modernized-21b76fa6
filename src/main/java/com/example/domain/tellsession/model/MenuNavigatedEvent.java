package com.example.domain.tellsession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }

    // Default constructor workaround for record if needed, but explicit constructor is fine.
    // Ensure consistent names with DomainEvent interface
}
