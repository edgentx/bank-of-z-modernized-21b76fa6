package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is ended.
 */
public class SessionEndedEvent implements DomainEvent {
    private final String aggregateId;
    private final Instant occurredAt;

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.ended";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionEndedEvent that = (SessionEndedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, occurredAt);
    }
}
