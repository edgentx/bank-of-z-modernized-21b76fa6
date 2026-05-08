package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Ensure defaults if constructor usage varies, though record handles this
    }
    
    // Factory method to match potential usage patterns if constructor isn't direct
    public static SessionStartedEvent create(String id, String tellerId, String terminalId, Instant time) {
        return new SessionStartedEvent(id, tellerId, terminalId, time);
    }

    @Override
    public String type() {
        return "session.started";
    }
}