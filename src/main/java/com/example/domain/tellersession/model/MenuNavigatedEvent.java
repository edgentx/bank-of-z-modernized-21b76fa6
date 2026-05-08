package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when the teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String action,
    String previousMenuId,
    String currentMenuId,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
