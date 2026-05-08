package com.example.domain.customer.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when customer details are successfully updated.
 * S-3: Implement UpdateCustomerDetailsCmd on Customer.
 */
public record CustomerDetailsUpdatedEvent(
        String customerId,
        String email,
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
