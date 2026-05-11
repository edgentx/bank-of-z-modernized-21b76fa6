package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record MenuNavigatedEvent(
    String type,
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this("menu.navigated", aggregateId, menuId, action, occurredAt);
    }
}
