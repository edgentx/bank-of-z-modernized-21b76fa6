package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public TellerSessionNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
