package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a parity check between legacy and modern stores is verified.
 * S-26.
 */
public record ParityVerifiedEvent(
        String aggregateId,
        String entityType,
        long syncOffset,
        String dateRange,
        String validationHash,
        Instant occurredAt,
        String eventId
) implements DomainEvent {

    public ParityVerifiedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "parity.verified";
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
