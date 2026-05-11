package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionInitiatedEvent(String sessionId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "session.initiated"; }
    @Override public String aggregateId() { return sessionId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
