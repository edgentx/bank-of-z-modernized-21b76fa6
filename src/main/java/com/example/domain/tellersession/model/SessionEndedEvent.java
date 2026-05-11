package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String type,
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent(String aggregateId) {
        this("SessionEndedEvent", aggregateId, Instant.now());
    }
}