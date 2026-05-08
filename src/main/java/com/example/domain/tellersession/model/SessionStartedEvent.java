package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public class SessionStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
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
        return aggregateId;
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
