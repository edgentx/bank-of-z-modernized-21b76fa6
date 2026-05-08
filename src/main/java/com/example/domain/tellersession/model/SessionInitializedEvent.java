package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a session is authenticated and initialized.
 */
public record SessionInitializedEvent(
    String aggregateId,
    String tellerId,
    String initialMenuId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.initialized";
    }
}