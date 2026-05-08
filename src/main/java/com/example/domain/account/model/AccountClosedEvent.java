package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class AccountClosedEvent implements DomainEvent {
    private final String event_id;
    private final String aggregateId;
    private final Instant occurredAt;

    public AccountClosedEvent(String aggregateId, Instant occurredAt) {
        this.event_id = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.closed";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getEventId() {
        return event_id;
    }
}
