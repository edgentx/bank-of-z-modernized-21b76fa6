package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String targetMenuId,
    String action,
    String previousContext,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action, String previousContext, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, targetMenuId, action, previousContext, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
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
