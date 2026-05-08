package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
  String aggregateId,
  Instant occurredAt
) implements DomainEvent {

  public SessionEndedEvent {
    // Validate nulls
    if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
  }

  @Override
  public String type() {
    return "session.ended";
  }

  @Override
  public String aggregateId() {
    return aggregateId;
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }
}
