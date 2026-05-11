package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionCreatedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "session.created"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}