package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record MenuNavigatedEvent(String type, String aggregateId, Instant occurredAt, String menuId, String action) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this("teller.session.menu.navigated", aggregateId, Instant.now(), menuId, action);
    }
}
