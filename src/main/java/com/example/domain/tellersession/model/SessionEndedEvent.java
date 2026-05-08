package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is terminated.
 * S-20: Used to trigger cache clears and audit trails.
 */
public record SessionEndedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }
}
