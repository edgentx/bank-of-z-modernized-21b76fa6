package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is successfully started.
 */
public class SessionStartedEvent implements DomainEvent {

    private final String eventId;
    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.sessionId = sessionId;
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
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
}
