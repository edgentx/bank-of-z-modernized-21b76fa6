package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event emitted when a Teller successfully navigates to a new menu.
 */
public class MenuNavigatedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String previousMenuId;
    private final String currentMenuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String tellerId, String previousMenuId, String currentMenuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.previousMenuId = previousMenuId;
        this.currentMenuId = currentMenuId;
        this.action = action;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getTellerId() {
        return tellerId;
    }

    public String getPreviousMenuId() {
        return previousMenuId;
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuNavigatedEvent that = (MenuNavigatedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(tellerId, that.tellerId) &&
                Objects.equals(previousMenuId, that.previousMenuId) &&
                Objects.equals(currentMenuId, that.currentMenuId) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, tellerId, previousMenuId, currentMenuId, action);
    }
}