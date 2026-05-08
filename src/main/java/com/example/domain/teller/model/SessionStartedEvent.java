package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller Session is successfully started.
 * Context: S-18 Implement StartSessionCmd on TellerSession.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        TellerSessionState navigationContext,
        Instant occurredAt
) implements DomainEvent {

    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
