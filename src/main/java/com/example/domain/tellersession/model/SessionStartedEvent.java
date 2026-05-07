package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String navigationState,
        int timeoutMinutes,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        // Ensure occurredAt is set if not provided, though record constructor handles this mostly
    }

    public SessionStartedEvent(String id, String tId, String term, String nav, int timeout, Instant time) {
        this.aggregateId = id;
        this.tellerId = tId;
        this.terminalId = term;
        this.navigationState = nav;
        this.timeoutMinutes = timeout;
        this.occurredAt = time;
    }

    @Override
    public String type() {
        return "session.started";
    }
}
