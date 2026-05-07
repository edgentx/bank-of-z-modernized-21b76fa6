package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(
    String aggregateId,
    String type,
    Instant occurredAt,
    String tellerId,
    String terminalId,
    String navState
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this(aggregateId, "SessionStarted", occurredAt, tellerId, terminalId, "HOME");
    }
}
