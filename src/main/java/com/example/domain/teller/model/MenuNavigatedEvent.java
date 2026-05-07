package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuNavigatedEvent that = (MenuNavigatedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(menuId, that.menuId) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, menuId, action);
    }
}