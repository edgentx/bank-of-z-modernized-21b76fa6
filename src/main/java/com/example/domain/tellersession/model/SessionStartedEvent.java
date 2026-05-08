package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public class SessionStartedEvent implements DomainEvent {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.tellerId = Objects.requireNonNull(tellerId);
        this.terminalId = Objects.requireNonNull(terminalId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
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

    public String tellerId() {
        return tellerId;
    }

    public String terminalId() {
        return terminalId;
    }
}
