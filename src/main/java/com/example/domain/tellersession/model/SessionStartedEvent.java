package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a teller session is successfully started.
 * S-18: session.started
 */
public class SessionStartedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final String navigationContext;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String navigationContext, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.navigationContext = navigationContext;
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
    public String navigationContext() { return navigationContext; }
}
