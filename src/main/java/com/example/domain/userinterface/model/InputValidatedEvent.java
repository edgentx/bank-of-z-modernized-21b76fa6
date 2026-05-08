package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated against
 * the specific legacy BMS constraints of the screen map.
 */
public record InputValidatedEvent(
    String aggregateId,
    String screenId,
    Map<String, String> inputFields,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "input.validated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
