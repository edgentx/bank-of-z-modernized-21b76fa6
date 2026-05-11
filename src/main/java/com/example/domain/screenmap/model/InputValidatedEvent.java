package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record InputValidatedEvent(
  String aggregateId,
  String screenId,
  Instant occurredAt
) implements DomainEvent {
  public InputValidatedEvent {
    // Ensure not null
  }
  
  public InputValidatedEvent(String aggregateId, String screenId, Instant occurredAt) {
    this.aggregateId = aggregateId;
    this.screenId = screenId;
    this.occurredAt = occurredAt;
  }

  @Override public String type() { return "input.validated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}