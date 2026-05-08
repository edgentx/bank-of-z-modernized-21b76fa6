package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input has been successfully validated against the map constraints.
 */
public record ScreenInputValidatedEvent(
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
        return screenId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
