package com.example.domain.ui.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
  String aggregateId,
  String tellerId,
  String terminalId,
  Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "session.started"; }
  @Override public Instant occurredAt() { return occurredAt; }
}
