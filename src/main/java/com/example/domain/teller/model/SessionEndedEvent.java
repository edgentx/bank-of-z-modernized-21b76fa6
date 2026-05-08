package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Domain event emitted when a teller session is terminated.
 * S-20: Implement EndSessionCmd on TellerSession.
 */
public record SessionEndedEvent(
        String aggregateId,
        String sessionId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }
}
