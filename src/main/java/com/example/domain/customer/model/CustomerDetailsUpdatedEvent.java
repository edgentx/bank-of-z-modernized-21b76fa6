package com.example.domain.customer.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record CustomerDetailsUpdatedEvent(
    String customerId,
    String fullName,
    String emailAddress,
    String governmentId,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "customer.details.updated";
    }

    @Override
    public String aggregateId() {
        return customerId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}