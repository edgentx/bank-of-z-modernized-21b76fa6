package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenInputValidatedEvent(
        String aggregateId,
        String screenId,
        Map<String, String> inputs,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "input.validated";
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