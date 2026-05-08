package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(String type, UUID sessionId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() { return type; }

    @Override
    public String aggregateId() { return sessionId.toString(); }

    @Override
    public Instant occurredAt() { return occurredAt; }
}
