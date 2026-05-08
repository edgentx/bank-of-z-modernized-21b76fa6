package com.example.domain.tellersession.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is successfully started.
 * Consolidated canonical definition per S-18.
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

    // aggregateId and occurredAt are explicitly defined in the constructor, matching interface methods.
}
