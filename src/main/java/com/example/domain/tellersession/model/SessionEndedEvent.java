package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a TellerSession is successfully terminated.
 */
public record SessionEndedEvent(
    String aggregateId,
    String endedBy,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        // Basic validation
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (endedBy == null || endedBy.isBlank()) {
            throw new IllegalArgumentException("endedBy (tellerId) cannot be null");
        }
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
