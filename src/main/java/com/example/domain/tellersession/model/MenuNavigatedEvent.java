package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String previousMenuId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Factory method to match previous patterns (record canonical ctor is strict)
    public static MenuNavigatedEvent create(String aggregateId, String prev, String target, String action, Instant now) {
        return new MenuNavigatedEvent(UUID.randomUUID().toString(), aggregateId, prev, target, action, now);
    }
}
