package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 * Indicates that the input adheres to BMS constraints and mandatory field rules.
 */
public record InputValidatedEvent(
        String aggregateId,
        Instant occurredAt,
        String screenId,
        Map<String, String> inputFields
) implements DomainEvent {

    public InputValidatedEvent {
        // Ensure immutability for the event payload
        if (inputFields != null) {
            inputFields = Map.copyOf(inputFields);
        }
        // Defensive copy for ID is not strictly necessary for Strings (immutable) but good practice if not final record
    }

    @Override
    public String type() {
        return "screen.input.validated";
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
