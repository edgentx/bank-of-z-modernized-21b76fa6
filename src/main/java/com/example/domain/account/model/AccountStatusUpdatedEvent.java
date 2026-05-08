package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AccountStatusUpdatedEvent(
        String eventId,
        String aggregateId,
        String accountNumber,
        String oldStatus,
        String newStatus,
        Instant occurredAt
) implements DomainEvent {
    public AccountStatusUpdatedEvent(String aggregateId, String accountNumber, String oldStatus, String newStatus, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, accountNumber, oldStatus, newStatus, occurredAt);
    }

    @Override
    public String type() {
        return "account.status.updated";
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