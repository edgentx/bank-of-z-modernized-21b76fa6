package com.example.domain.tellsession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    String screen,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
