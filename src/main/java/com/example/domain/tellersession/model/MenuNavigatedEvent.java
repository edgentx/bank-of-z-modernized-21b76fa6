package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

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

    // Constructor to enforce OccurredAt if not provided
    public MenuNavigatedEvent {
        if (occurredAt == null) occurredAt = Instant.now();
    }
}
