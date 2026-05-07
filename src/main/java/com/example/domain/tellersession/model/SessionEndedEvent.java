package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is ended.
 * S-20: Implement EndSessionCmd.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }
}
