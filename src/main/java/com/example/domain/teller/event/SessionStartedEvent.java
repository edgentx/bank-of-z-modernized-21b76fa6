package com.example.domain.teller.event;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is started.
 * Story S-18: SessionStartedEvent
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    String navigationState,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    // Override aggregateId to match record field name if needed, or map constructor.
    // The DomainEvent interface expects `aggregateId()`. The record has `aggregateId` field.
    // This matches perfectly.
}
