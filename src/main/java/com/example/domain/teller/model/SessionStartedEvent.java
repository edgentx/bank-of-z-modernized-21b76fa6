package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

// Helper event to set up the aggregate state for testing
public record SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }
}
