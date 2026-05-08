package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Set;

public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Set<String> roles,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}