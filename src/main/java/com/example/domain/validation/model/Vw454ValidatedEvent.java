package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record Vw454ValidatedEvent(
    String aggregateId,
    String status,
    String gitHubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "Vw454Validated";
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
