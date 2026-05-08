package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a Teller Session is started/authenticated.
 */
public record TellerLoggedInEvent(
    String aggregateId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {

    // Canonical constructor required by Aggregate logic
    public TellerLoggedInEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "teller.logged.in";
    }
}