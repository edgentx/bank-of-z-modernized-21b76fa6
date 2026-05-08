package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AccountStatusUpdatedEvent(
    String eventId,
    String aggregateId,
    AccountStatus oldStatus,
    AccountStatus newStatus,
    Instant occurredAt
) implements DomainEvent {

    public AccountStatusUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public AccountStatusUpdatedEvent(String aggregateId, AccountStatus oldStatus, AccountStatus newStatus) {
        this(null, aggregateId, oldStatus, newStatus, Instant.now());
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
