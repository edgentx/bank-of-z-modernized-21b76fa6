package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record CheckpointRecordedEvent(String aggregateId, long syncOffset, String validationHash, Instant occurredAt) implements DomainEvent {
    public CheckpointRecordedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(validationHash, "validationHash required");
    }

    @Override
    public String type() {
        return "checkpoint.recorded";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}