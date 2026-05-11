package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record ScreenInputValidatedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    Map<String, String> inputFields
) implements DomainEvent {
    public ScreenInputValidatedEvent(String aggregateId, Map<String, String> inputFields) {
        this("input.validated", aggregateId, Instant.now(), inputFields);
    }
}