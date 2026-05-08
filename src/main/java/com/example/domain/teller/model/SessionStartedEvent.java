package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(String sessionId, String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}
