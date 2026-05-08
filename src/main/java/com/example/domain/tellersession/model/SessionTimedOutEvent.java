package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a session times out due to inactivity.
 * This event results from a timeout check command or scheduler, but is defined here for completeness.
 */
public record SessionTimedOutEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.timed_out";
    }
}
