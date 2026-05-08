package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(
    String type,
    String aggregateId,
    String tellerId,
    Instant occurredAt,
    String terminalId
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this("SessionStartedEvent", aggregateId, tellerId, occurredAt, terminalId);
    }
}
