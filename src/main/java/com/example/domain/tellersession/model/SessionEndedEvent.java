package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class SessionEndedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final Instant occurredAt;

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
