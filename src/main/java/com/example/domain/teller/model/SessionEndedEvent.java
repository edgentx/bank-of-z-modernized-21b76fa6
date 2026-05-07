package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public class SessionEndedEvent implements DomainEvent {
    private final String sessionId;
    private final String tellerId;
    private final Instant occurredAt;

    public SessionEndedEvent(String sessionId, String tellerId, Instant occurredAt) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
}
