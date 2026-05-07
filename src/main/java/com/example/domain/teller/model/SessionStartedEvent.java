package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is successfully started.
 * S-18: Session Started Event
 */
public class SessionStartedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final String navigationState;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String navigationState, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.navigationState = navigationState;
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
    public String navigationState() { return navigationState; }
}
