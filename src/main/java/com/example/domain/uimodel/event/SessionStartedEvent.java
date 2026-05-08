package com.example.domain.uimodel.event;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class SessionStartedEvent implements DomainEvent {
    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final String context;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String context, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.context = context;
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

    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public String getContext() { return context; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionStartedEvent that = (SessionStartedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(tellerId, that.tellerId) && Objects.equals(terminalId, that.terminalId) && Objects.equals(context, that.context) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, tellerId, terminalId, context, occurredAt);
    }
}