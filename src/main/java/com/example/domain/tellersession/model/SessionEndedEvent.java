package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is ended.
 * Context: User-Interface-Navigation (S-20).
 */
public class SessionEndedEvent implements DomainEvent {

    private final String aggregateId;
    private final Instant occurredAt;
    private final String eventType = "session.ended";

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return eventType;
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
