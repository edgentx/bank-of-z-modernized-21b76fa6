package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ScreenInputValidatedEvent(
        String eventId,
        String screenId,
        Map<String, String> inputFields,
        Instant occurredAt
) implements DomainEvent {

    public ScreenInputValidatedEvent(String screenId, Map<String, String> inputFields, Instant occurredAt) {
        this(UUID.randomUUID().toString(), screenId, inputFields, occurredAt);
    }

    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public String aggregateId() {
        return screenId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
