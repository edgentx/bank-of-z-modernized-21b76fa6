package com.example.domain.customer.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;

public record CustomerDetailsUpdatedEvent(
    String customerId, 
    String fullName, 
    String email, 
    LocalDate dateOfBirth, 
    String sortCode, 
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "customer.details.updated"; }
    @Override public String aggregateId() { return customerId; }
}
