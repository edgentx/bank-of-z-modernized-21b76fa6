package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Domain event emitted when a teller session is initiated.
 * S-20: Supporting event to establish the 'Started' state for EndSessionCmd validation.
 */
public record SessionInitiatedEvent(
        String aggregateId,
        String sessionId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.initiated";
    }
}
