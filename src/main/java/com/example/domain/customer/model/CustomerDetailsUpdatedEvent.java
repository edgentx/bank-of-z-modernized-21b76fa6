package com.example.domain.customer.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when customer details are successfully updated.
 * S-3: UpdateCustomerDetailsCmd.
 */
public record CustomerDetailsUpdatedEvent(
        String customerId,
        String emailAddress,
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
