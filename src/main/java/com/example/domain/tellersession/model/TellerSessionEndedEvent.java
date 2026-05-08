package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is successfully ended.
 */
public record TellerSessionEndedEvent(
    String aggregateId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        // Ensure no nulls, though ideally these are valid from the aggregate
    }

    @Override
    public String type() {
        return "session.ended";
    }

    // Static factory for convenience if needed, but constructor is fine for records
    public static TellerSessionEndedEvent create(String sessionId, String tellerId) {
        return new TellerSessionEndedEvent(sessionId, tellerId, Instant.now());
    }
}
