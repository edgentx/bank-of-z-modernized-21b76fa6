package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record TellerSessionActivatedEvent(
        String aggregateId,
        String initialMenuId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionActivatedEvent {
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "session.activated";
    }
}