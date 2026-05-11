package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully initiated.
 */
public record SessionStartedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String tellerId,
    String terminalId,
    String sessionId
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(
            "SessionStartedEvent",
            aggregateId,
            Instant.now(),
            tellerId,
            terminalId,
            UUID.randomUUID().toString()
        );
    }
}
