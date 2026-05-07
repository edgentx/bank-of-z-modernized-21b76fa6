package com.example.domain.tellermgmt.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionStartedEvent(String id, String tellerId, String terminalId) {
        this(id, tellerId, terminalId, Instant.now());
    }
    @Override
    public String type() {
        return "teller.session.started";
    }
}
