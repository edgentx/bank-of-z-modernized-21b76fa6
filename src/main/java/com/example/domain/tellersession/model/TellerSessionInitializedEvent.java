package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Internal event to track state initialization for the aggregate.
 * Not exposed externally as a primary S-19 artifact, but necessary for aggregate logic.
 */
record TellerSessionInitializedEvent(String sessionId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "tellersession.initialized";
    }
    @Override
    public String aggregateId() {
        return sessionId;
    }
}