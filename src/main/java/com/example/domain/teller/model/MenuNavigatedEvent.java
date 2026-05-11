package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String targetMenu,
        String previousMenu,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId cannot be null or blank");
        }
    }

    public MenuNavigatedEvent(String aggregateId, String targetMenu, String previousMenu, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, targetMenu, previousMenu, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
