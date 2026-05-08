package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated against the map rules.
 */
public record InputValidatedEvent(
        String aggregateId,
        Map<String, String> validatedInput,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "input.validated";
    }
}
