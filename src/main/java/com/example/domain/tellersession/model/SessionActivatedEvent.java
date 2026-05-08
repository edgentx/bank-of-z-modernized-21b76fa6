package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionActivatedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.activated";
    }
    @Override
    public String aggregateId() { return aggregateId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
}