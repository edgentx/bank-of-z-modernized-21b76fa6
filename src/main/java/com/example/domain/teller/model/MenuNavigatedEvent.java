package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this(
                aggregateId != null ? aggregateId : UUID.randomUUID().toString(),
                menuId,
                action,
                occurredAt != null ? occurredAt : Instant.now()
        );
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
