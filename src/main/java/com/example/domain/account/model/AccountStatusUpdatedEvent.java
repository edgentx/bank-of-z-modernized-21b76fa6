package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

// Fix for Error: "return type of accessor method type() must match..."
public record AccountStatusUpdatedEvent(String aggregateId, AccountStatus newStatus, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "account.status.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
