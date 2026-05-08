package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 */
public record ScreenInputValidatedEvent(
    String aggregateId,
    String screenId,
    Map<String, String> inputFields,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "userinterfacenavigation.input.validated";
    }
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
