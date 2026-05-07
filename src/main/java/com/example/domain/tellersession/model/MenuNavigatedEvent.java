package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent(String aggregateId, String sessionId, String menuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId != null ? aggregateId : UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.menuId = menuId;
        this.action = action;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}