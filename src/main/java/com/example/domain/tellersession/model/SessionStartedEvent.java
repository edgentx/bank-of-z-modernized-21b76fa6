package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String eventId,
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt,
    String navigationState
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String navState, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, terminalId, occurredAt, navState);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
