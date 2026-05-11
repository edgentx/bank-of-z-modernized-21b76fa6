package com.example.domain.tellermaintenance.model.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        int timeoutInSeconds,
        String navigationContext,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}