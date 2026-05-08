package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerSessionEndedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "TellerSessionEndedEvent";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
