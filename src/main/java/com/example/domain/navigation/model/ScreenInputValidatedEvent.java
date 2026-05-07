package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Domain event emitted when screen input passes validation.
 */
public record ScreenInputValidatedEvent(
    String aggregateId,
    String screenId,
    Map<String, String> inputFields,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "input.validated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
