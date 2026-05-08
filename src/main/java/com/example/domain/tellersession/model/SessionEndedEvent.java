package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 */
public record SessionEndedEvent(
        UUID aggregateId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public UUID aggregateId() {
        return aggregateId;
    }
}
