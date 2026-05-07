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
        // Ensure aggregateId is never null, though in a real constructor it might be defaulted.
        // Here we assume the caller provides it.
    }

    @Override
    public String type() {
        return "session.started";
    }
}
