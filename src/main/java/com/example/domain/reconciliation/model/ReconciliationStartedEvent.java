package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record ReconciliationStartedEvent(
    String aggregateId,
    Instant windowStart,
    Instant windowEnd,
    Instant occurredAt
) implements DomainEvent {
  public ReconciliationStartedEvent {
    if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
  }

  @Override public String type() { return "reconciliation.started"; }
  @Override public String aggregateId() { return aggregateId; }
}