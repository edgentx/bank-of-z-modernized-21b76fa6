package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String sessionId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public MenuNavigatedEvent(String sessionId, String targetMenuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), sessionId, sessionId, targetMenuId, action, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}