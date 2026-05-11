package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when screen input is validated successfully.
 */
public record InputValidatedEvent(String aggregateId, Map<String, String> inputFields, Instant occurredAt) implements DomainEvent {
    public InputValidatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
