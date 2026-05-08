package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record MenuNavigatedEvent(String sessionId, String menuId, String action, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}