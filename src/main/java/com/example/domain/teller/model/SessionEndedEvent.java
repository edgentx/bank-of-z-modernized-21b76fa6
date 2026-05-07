package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }
    
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}