package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String tellerId,
        String terminalId
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this("session.started", aggregateId, Instant.now(), tellerId, terminalId);
    }
}
