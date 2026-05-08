package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    String sourceChannelId,
    String currentContext,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "SessionStartedEvent";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}