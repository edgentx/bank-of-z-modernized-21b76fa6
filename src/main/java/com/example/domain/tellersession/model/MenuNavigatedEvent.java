package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller successfully navigates to a new menu context.
 */
public class MenuNavigatedEvent implements DomainEvent {

    private final String type;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String menuId;
    private final String action;

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this.type = "menu.navigated";
        this.aggregateId = aggregateId;
        this.menuId = menuId;
        this.action = action;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String menuId() {
        return menuId;
    }

    public String action() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuNavigatedEvent that = (MenuNavigatedEvent) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(occurredAt, that.occurredAt) &&
                Objects.equals(menuId, that.menuId) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, aggregateId, occurredAt, menuId, action);
    }
}
