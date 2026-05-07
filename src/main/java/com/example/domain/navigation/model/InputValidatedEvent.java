package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record InputValidatedEvent(String aggregateId, Map<String, String> inputFields, Instant occurredAt) implements DomainEvent {
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
