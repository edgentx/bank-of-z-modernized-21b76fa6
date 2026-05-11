package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when screen input is successfully validated against
 * legacy BMS constraints and mandatory field rules.
 */
public record InputValidatedEvent(
        String eventId,
        String aggregateId, // ScreenMap ID
        String screenId,
        Map<String, String> inputFields,
        Instant occurredAt
) implements DomainEvent {
    public InputValidatedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
