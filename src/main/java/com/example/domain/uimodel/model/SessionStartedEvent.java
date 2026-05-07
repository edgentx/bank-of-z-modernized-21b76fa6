package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
  String aggregateId,
  String tellerId,
  String terminalId,
  Instant occurredAt
) implements DomainEvent {
  public SessionStartedEvent {
    // Ensure UUIDs are present
  }
  @Override public String type() { return "session.started"; }
  @Override public Instant occurredAt() { return occurredAt; }
}
