package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenuId,
        String actionTaken,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            aggregateId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
