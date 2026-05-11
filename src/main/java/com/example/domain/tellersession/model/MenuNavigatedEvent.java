package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a menu navigation is successfully executed.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
