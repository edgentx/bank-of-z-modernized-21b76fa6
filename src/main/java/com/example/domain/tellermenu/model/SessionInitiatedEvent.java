package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionInitiatedEvent(String aggregateId, String tellerId, String menuId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.initiated";
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
