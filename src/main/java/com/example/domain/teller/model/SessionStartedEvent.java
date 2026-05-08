package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
  public SessionStartedEvent {
    // Validate defensive copy if needed, though record validation is limited pre-construction
  }

  @Override public String type() { return "teller.session.started"; }

  @Override public Instant occurredAt() { return occurredAt; }

  public static SessionStartedEvent create(String sessionId, String tellerId, String terminalId) {
    return new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now());
  }
}
