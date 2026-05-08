package com.example.domain.tellersession.model;

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
    // Ensure aggregateId is never null, generate if necessary (though logic should handle it)
    if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
    if (occurredAt == null) occurredAt = Instant.now();
  }

  @Override public String type() { return "session.started"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
