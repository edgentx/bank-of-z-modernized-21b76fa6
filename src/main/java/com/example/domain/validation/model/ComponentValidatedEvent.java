package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record ComponentValidatedEvent(String validationId, String component, boolean passed, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "ComponentValidated"; }
    @Override public String aggregateId() { return validationId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
