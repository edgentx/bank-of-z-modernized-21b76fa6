package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a teller successfully navigates to a new screen.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String tellerId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}