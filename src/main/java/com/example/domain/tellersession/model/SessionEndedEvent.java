package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is terminated.
 * Context: S-20 Implement EndSessionCmd on TellerSession.
 */
public record SessionEndedEvent(
    String aggregateId,
    String tellerId,
    Instant terminatedAt
) implements DomainEvent {

    public SessionEndedEvent {
        if (terminatedAt == null) {
            terminatedAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return terminatedAt;
    }
}
