package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller successfully navigates to a new menu.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }

    // Constructor overload for backward compatibility if needed, though record handles it
    public static MenuNavigatedEvent create(String sessionId, String menuId, String action) {
        return new MenuNavigatedEvent(sessionId, menuId, action, Instant.now());
    }
}
