package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when user input has been successfully validated
 * against the legacy BMS map constraints.
 */
public record ScreenInputValidatedEvent(
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
