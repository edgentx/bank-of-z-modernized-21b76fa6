package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 */
public record InputValidatedEvent(
    String aggregateId,
    Map<String, String> validatedFields,
    Instant occurredAt
) implements DomainEvent {

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
