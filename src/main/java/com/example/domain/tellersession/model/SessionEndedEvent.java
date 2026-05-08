package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 * S-20: Implement EndSessionCmd on TellerSession
 */
public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
