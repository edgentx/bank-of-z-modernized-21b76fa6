package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
  public SessionStartedEvent {
    // Ensure the event has a unique ID correlation if needed, or just use aggregateId
  }
  @Override public String type() { return "teller.session.started"; }
  @Override public String aggregateId() { return aggregateId; }
}
