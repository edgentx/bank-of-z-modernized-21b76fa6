package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event published when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String actionTaken,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (targetMenuId == null) throw new IllegalArgumentException("targetMenuId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
