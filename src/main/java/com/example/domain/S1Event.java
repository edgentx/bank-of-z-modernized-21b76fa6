package com.example.domain;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Sample Event for the S-1 BDD scenarios.
 */
public record S1Event(String type, String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
