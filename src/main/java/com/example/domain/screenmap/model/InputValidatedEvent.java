package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when input is successfully validated against a ScreenMap.
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
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
