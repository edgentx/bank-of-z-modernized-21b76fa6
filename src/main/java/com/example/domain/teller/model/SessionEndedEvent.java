package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
  UUID sessionId,
  String tellerId,
  Instant endedAt
) implements DomainEvent {
  @Override public String type() { return "session.ended"; }
  @Override public String aggregateId() { return sessionId.toString(); }
  @Override public Instant occurredAt() { return endedAt; }
}
