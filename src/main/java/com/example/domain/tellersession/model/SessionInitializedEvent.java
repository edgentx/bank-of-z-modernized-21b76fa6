package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event representing the start of a session.
 * Used here primarily to support hydration for test scenarios.
 */
public record SessionInitializedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.initialized";
    }
}
