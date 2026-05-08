package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a Teller Session is successfully terminated.
 */
public record SessionEndedEvent(
    String type,
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this("session.ended", aggregateId, occurredAt);
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
