package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
  String aggregateId,
  String eventType,
  String tellerId,
  String terminalId,
  Instant occurredAt
) implements DomainEvent {
  public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
    this(aggregateId, "SessionStartedEvent", tellerId, terminalId, occurredAt);
  }
  @Override public String type() { return eventType; }
  @Override public String aggregateId() { return aggregateId; }
}
