package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing a change in the teller's navigation context (screen/function).
 */
public record NavigationChangedEvent(
        String aggregateId,
        String functionId,
        String screenId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "NavigationChangedEvent";
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
