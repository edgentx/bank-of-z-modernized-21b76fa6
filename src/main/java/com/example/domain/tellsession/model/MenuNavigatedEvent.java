package com.example.domain.tellsession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) implements DomainEvent {
    public MenuNavigatedEvent {
        // Defensive copy/validation if needed
    }
    @Override
    public String type() {
        return "teller.session.menu.navigated";
    }
    @Override
    public String aggregateId() {
        return aggregateId;
    }
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
