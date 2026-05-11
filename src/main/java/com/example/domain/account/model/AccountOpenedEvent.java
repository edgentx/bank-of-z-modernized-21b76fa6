package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

// Fix for Error: "return type of accessor method type() must match..."
// Records implicitly generate accessor methods with the same name as the component.
// If the component is named 'type', it returns Class<?> (or whatever type 'type' is).
// The DomainEvent interface requires a method type() that returns String.
// This causes a conflict if the record component is named 'type'.
// Solution: Rename component to 'eventType' or implement explicit accessor.
public record AccountOpenedEvent(
        String aggregateId,
        String customerId,
        AccountStatus status,
        Instant occurredAt
) implements DomainEvent {

    // Explicit implementation of DomainEvent.type()
    // Note: We do NOT have a record component named 'type', so no conflict.
    @Override
    public String type() {
        return "account.opened";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
