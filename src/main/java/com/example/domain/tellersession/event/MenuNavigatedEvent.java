package com.example.domain.tellersession.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller successfully navigates to a new screen/menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String currentMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public String type() {
        return "menu.navigated";
    }
}