package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String layout,
    Instant occurredAt,
    String deviceType,
    String accountId,
    Map<String, Object> context
) implements DomainEvent {
    
    @Override
    public String type() {
        return "screen.rendered";
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