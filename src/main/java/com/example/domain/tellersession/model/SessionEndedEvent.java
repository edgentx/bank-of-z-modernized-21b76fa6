package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionEndedEvent(
    String aggregateId,
    String sessionId,
    Instant occurredAt
) implements DomainEvent {
  public SessionEndedEvent(String sessionId) {
    this(UUID.randomUUID().toString(), sessionId, Instant.now());
  }
  @Override public String type() { return "session.ended"; }
}