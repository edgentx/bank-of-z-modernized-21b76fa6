package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller Session is successfully started.
 * S-18: session.started
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

    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
}
