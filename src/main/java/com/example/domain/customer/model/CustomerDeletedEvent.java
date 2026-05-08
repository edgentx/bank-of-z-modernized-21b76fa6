package com.example.domain.customer.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a customer is deleted.
 */
public record CustomerDeletedEvent(String customerId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "customer.deleted";
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
