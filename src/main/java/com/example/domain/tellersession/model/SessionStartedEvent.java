package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 * S-18
 */
public class SessionStartedEvent implements DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
}
