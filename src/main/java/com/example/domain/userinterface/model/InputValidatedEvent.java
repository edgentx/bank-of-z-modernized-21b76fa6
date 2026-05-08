package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when screen input successfully passes validation rules.
 * Signals that the request can proceed to backend command routing.
 */
public record InputValidatedEvent(
        String aggregateId,
        String screenId,
        Map<String, String> validatedFields,
        Instant occurredAt
) implements DomainEvent {

    public InputValidatedEvent {
        // Ensure non-nulls for immutability
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "screen.validated";
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
