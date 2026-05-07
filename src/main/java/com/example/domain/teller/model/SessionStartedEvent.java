package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller Session is started.
 * S-18: user-interface-navigation.
 */
public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String terminalId,
        String navigationState,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String navigationState, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, terminalId, navigationState, occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
