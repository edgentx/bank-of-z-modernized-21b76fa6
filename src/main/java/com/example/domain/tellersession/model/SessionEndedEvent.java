package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a TellerSession is terminated.
 * Fixed record definition to match acceptance criteria and constructor usage.
 */
public record SessionEndedEvent(String aggregateId, String tellerId, Instant occurredAt) implements DomainEvent {
    public SessionEndedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "session.ended";
    }
}
