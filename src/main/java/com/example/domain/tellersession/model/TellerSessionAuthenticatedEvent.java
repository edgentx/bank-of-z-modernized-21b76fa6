package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Internal domain event representing the session start.
 * Used to set up the valid state for S-19 tests.
 */
public record TellerSessionAuthenticatedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "tellersession.authenticated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
