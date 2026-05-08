package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when screen input is successfully validated.
 * S-22 User-Interface-Navigation.
 */
public record InputValidatedEvent(
        String eventId,
        String aggregateId,
        String screenId,
        Map<String, String> validatedFields,
        Instant occurredAt
) implements DomainEvent {

    public InputValidatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public InputValidatedEvent(String aggregateId, String screenId, Map<String, String> validatedFields, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, screenId, validatedFields, occurredAt);
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
