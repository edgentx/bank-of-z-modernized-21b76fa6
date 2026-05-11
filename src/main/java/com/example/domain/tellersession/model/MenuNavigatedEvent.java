package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String sessionId,
        String menuId,
        String action
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this("menu.navigated", aggregateId, Instant.now(), aggregateId, menuId, action);
    }
}