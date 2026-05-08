package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
  public SessionEndedEvent {
    if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
  }

  public SessionEndedEvent(String aggregateId) {
    this(aggregateId, Instant.now());
  }

  @Override public String type() { return "session.ended"; }
  @Override public String aggregateId() { return aggregateId; }
}