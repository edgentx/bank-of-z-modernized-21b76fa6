package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a TellerSession is terminated.
 * S-20: EndSessionCmd on TellerSession.
 */
public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId) {
        this(aggregateId, Instant.now());
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
