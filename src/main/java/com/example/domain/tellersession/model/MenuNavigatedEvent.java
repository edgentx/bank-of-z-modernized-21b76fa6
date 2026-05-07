package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, targetMenuId, action, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
