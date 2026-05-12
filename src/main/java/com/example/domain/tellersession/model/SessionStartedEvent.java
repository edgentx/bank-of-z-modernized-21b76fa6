package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionStartedEvent(
  String type,
  String aggregateId,
  Instant occurredAt,
  String tellerId,
  String terminalId
) implements DomainEvent {
  public SessionStartedEvent {
    if (type == null) type = "SessionStartedEvent";
  }

  public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
    return new SessionStartedEvent("SessionStartedEvent", aggregateId, Instant.now(), tellerId, terminalId);
  }
}