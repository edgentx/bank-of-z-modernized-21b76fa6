package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String sessionId, String menuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, sessionId, menuId, action, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
