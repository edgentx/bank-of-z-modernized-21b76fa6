package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class SessionEndedEvent implements DomainEvent {

    private final String aggregateId;
    private final String tellerId;
    private final Instant occurredAt;

    public SessionEndedEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
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

    public String getTellerId() {
        return tellerId;
    }
}