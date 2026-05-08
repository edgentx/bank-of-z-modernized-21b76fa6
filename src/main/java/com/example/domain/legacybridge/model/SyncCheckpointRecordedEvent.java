package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SyncCheckpointRecordedEvent(
    String eventId,
    String aggregateId,
    long syncOffset,
    String validationHash,
    Instant occurredAt
) implements DomainEvent {
    public SyncCheckpointRecordedEvent(String aggregateId, long syncOffset, String validationHash, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, syncOffset, validationHash, occurredAt);
    }

    @Override
    public String type() {
        return "checkpoint.recorded";
    }
}
