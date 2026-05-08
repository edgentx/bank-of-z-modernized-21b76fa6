package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when screen input successfully passes validation.
 */
public record InputValidatedEvent(
    String aggregateId,
    String screenId,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "input.validated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
