package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
