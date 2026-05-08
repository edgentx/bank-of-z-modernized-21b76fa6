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
    // Ensure immutability if needed, though records handle most of it
  }
  
  @Override
  public String type() {
    return "session.started";
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }
  
  // Factory method to match existing aggregate pattern if needed, or just use constructor
  public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
      return new SessionStartedEvent(aggregateId, tellerId, terminalId, Instant.now());
  }
}
