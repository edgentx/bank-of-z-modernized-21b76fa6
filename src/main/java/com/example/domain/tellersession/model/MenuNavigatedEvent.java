package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when the teller successfully navigates to a new menu/screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenu,
        String previousMenu,
        String action,
        Instant occurredAt,
        String eventId
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public MenuNavigatedEvent(String aggregateId, String targetMenu, String previousMenu, String action, Instant occurredAt) {
        this(aggregateId, targetMenu, previousMenu, action, occurredAt, UUID.randomUUID().toString());
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
