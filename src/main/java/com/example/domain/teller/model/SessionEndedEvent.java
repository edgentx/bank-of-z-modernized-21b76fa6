package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionEndedEvent(String type, String aggregateId, Instant occurredAt) implements DomainEvent {
    public SessionEndedEvent {
        if (type == null) type = "session.ended";
    }

    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this("session.ended", aggregateId, occurredAt);
    }
}
