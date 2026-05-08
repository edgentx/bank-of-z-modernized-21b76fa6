package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event representing the successful login of a teller.
 * Used here primarily to set up the aggregate state for testing S-19 logic.
 */
public record TellerLoggedInEvent(
    String sessionId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "teller.logged.in";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
