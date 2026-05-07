package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
  public SessionStartedEvent {
    // Ensure occurredAt is set if not provided, though we prefer explicit passing
  }
  @Override public String type() { return "teller.session.started"; }
  @Override public String aggregateId() { return sessionId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
