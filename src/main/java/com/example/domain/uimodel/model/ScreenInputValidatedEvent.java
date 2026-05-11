package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when screen input is successfully validated.
 */
public record ScreenInputValidatedEvent(String aggregateId, Map<String, String> inputFields, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
