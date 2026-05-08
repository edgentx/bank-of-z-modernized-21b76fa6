package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller successfully navigates to a menu.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}