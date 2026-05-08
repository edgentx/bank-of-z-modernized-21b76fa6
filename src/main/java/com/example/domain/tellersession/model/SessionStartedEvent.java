package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller Session is successfully started.
 */
public class SessionStartedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final String terminalId;
    private final Instant timeoutAt;
    private final Instant occurredAt;

    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant timeoutAt, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.timeoutAt = timeoutAt;
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

    public String getTellerId() {
        return tellerId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public Instant getTimeoutAt() {
        return timeoutAt;
    }
}
