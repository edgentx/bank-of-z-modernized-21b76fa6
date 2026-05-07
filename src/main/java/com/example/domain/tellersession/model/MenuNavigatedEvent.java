package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenu,
    String targetMenu,
    String action,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Standardizing occurrence timestamp to ensure consistency
    public MenuNavigatedEvent {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
