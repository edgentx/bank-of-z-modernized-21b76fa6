package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Domain event emitted when user input is successfully validated against a ScreenMap.
 * Indicates that the input is safe to be routed to backend command handlers.
 */
public record ScreenInputValidatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String screenId,
        Map<String, String> inputFields
) implements DomainEvent {

    public ScreenInputValidatedEvent(String aggregateId, String screenId, Map<String, String> inputFields) {
        this("input.validated", aggregateId, Instant.now(), screenId, inputFields);
    }

    @Override
    public String type() {
        return type;
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
