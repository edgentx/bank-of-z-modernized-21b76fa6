package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is started.
 */
public class SessionStartedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final String context;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String context, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.context = context;
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

    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public String getContext() { return context; }
}
