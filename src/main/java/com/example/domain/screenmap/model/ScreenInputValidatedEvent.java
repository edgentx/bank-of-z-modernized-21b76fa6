package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenInputValidatedEvent(String screenId, Instant occurredAt) implements DomainEvent {
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
