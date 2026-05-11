package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input has been successfully validated
 * against the screen map rules (constraints and mandatory fields).
 */
public record InputValidatedEvent(
        String aggregateId,
        String screenId,
        Map<String, String> inputFields,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
