package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event representing the start/activation of the session (post-authentication).
 */
public class TellerSessionStartedEvent implements DomainEvent {
    private final String sessionId;
    private final Instant occurredAt;

    public TellerSessionStartedEvent(String sessionId, Instant occurredAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "tellersession.started";
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
