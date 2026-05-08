package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }
    @Override
    public String type() {
        return "menu.navigated";
    }
}
