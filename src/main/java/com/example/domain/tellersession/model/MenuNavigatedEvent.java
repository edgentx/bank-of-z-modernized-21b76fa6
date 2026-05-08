package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
