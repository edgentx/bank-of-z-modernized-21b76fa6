package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when screen input is successfully validated against the ScreenMap rules.
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
}
