package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenInputValidatedEvent(
    String aggregateId,
    Map<String, String> inputFields,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ScreenInputValidatedEvent";
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
