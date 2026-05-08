package com.example.domain.tellermachine.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully started.
 * Part of Story S-18.
 */
public record SessionStartedEvent(
    String aggregateId,
    String type,
    Instant occurredAt,
    String tellerId,
    String terminalId,
    String navigationState
) implements DomainEvent {

    public SessionStartedEvent(String sessionId, String tellerId, String terminalId, String navState) {
        this(
            sessionId,
            "session.started",
            Instant.now(),
            tellerId,
            terminalId,
            navState
        );
    }
}
