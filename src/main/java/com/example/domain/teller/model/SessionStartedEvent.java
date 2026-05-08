package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class SessionStartedEvent implements DomainEvent {
    private final String sessionId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) {
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

    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
}