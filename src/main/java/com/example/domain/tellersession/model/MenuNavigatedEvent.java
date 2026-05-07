package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        String screenNavigationId,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (screenNavigationId == null) screenNavigationId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "teller.menu.navigated";
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
