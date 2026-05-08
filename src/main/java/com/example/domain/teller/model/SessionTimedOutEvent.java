package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionTimedOutEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() { return "session.timedout"; }
    @Override
    public String aggregateId() { return aggregateId; }
    @Override
    public Instant occurredAt() { return occurredAt; }
}
