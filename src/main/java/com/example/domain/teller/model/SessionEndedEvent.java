package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a TellerSession is ended.
 */
public record SessionEndedEvent(
        String aggregateId,
        String eventType,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this(aggregateId, "session.ended", occurredAt);
    }

    @Override
    public String type() {
        return eventType;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
