package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Set;

/**
 * Event representing the creation of a ScreenMap definition.
 * Used to bootstrap the aggregate for this feature context.
 */
public record ScreenMapCreatedEvent(
        String aggregateId,
        String screenName,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screenmap.created";
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
