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

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    public static MenuNavigatedEvent create(String aggregateId, String menuId, String action) {
        return new MenuNavigatedEvent(aggregateId, menuId, action, Instant.now());
    }
}
