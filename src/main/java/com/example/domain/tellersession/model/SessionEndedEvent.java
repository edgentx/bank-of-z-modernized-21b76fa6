package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionEndedEvent(
  String type,
  String aggregateId,
  Instant occurredAt,
  String sessionId
) implements DomainEvent {
  public SessionEndedEvent {
    if (type == null) type = "session.ended";
  }

  public static SessionEndedEvent create(String aggregateId, String sessionId) {
    return new SessionEndedEvent("session.ended", aggregateId, Instant.now(), sessionId);
  }
}
