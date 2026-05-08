package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import com.example.domain.ui.model.StartSessionCmd;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public class SessionStartedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String tellerId() {
        return tellerId;
    }

    public String terminalId() {
        return terminalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionStartedEvent that = (SessionStartedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(tellerId, that.tellerId) && Objects.equals(terminalId, that.terminalId) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, tellerId, terminalId, occurredAt);
    }
}
