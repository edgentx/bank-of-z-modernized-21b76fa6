package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (menuId == null) throw new IllegalArgumentException("menuId required");
        if (action == null) throw new IllegalArgumentException("action required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
