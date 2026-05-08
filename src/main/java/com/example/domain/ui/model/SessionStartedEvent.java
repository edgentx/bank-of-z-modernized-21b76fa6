package com.example.domain.ui.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    public SessionStartedEvent {
        // Ensure defaults if needed, though constructor enforces it
    }

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}