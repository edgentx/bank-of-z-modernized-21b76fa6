package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

// Record implementing DomainEvent
// Explicitly implementing accessor methods to satisfy interface return types if generics differ, 
// though here we align with String/Instant.
public record AccountStatusUpdatedEvent(
    String aggregateId,
    String oldStatus,
    String newStatus,
    Instant occurredAt
) implements DomainEvent {

    // DomainEvent requires type() -> String
    @Override
    public String type() {
        return "account.status.updated";
    }

    // DomainEvent requires aggregateId() -> String (Matches record component)
    // Record accessor is public, implicit, or explicit. We rely on the implicit one or name match.
    // The record component is named 'aggregateId', which matches the method name required by DomainEvent.
    
    // DomainEvent requires occurredAt() -> Instant (Matches record component)
    // The record component is named 'occurredAt', matching the method name.
}
