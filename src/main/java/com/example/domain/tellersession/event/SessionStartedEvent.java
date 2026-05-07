package com.example.domain.tellersession.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    // Constructor to handle synthetic field mismatch if needed, but record matches interface
}
