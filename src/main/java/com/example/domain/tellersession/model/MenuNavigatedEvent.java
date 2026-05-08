package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String targetMenu,
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

    // Convenience alias for Gherkin/JSON mapping
    public String targetMenu() {
        return targetMenu;
    }
}
