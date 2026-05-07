package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event representing the successful authentication of a teller.
 */
public class TellerSessionAuthenticatedEvent implements DomainEvent {
    private final String sessionId;
    private final String tellerId;
    private final Instant occurredAt;

    public TellerSessionAuthenticatedEvent(String sessionId, String tellerId, Instant occurredAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.tellerId = Objects.requireNonNull(tellerId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "tellersession.authenticated";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getTellerId() { return tellerId; }
}
