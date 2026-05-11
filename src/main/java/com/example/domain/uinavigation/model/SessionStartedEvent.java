package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String navigationContext,
        Instant occurredAt
) implements DomainEvent {

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
}
