package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 */
public record InputValidatedEvent(String aggregateId, Map<String, String> inputFields, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "input.validated";
    }
}