package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 * Signals that the input adheres to BMS length constraints and mandatory field rules.
 */
public record ScreenInputValidatedEvent(
        String aggregateId,
        Map<String, String> inputFields,
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