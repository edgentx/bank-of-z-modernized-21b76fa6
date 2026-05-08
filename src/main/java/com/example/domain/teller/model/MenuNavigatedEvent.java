package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String previousMenuId,
        String currentMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Factory for test convenience if needed, or just use constructor
    public static MenuNavigatedEvent create(String sessionId, String prev, String curr, String action, Instant time) {
        return new MenuNavigatedEvent(sessionId, prev, curr, action, time);
    }
}