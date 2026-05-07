package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record TellerSessionEndedEvent(
    String sessionId, String tellerId, Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "teller.session.ended"; }
  @Override public String aggregateId() { return sessionId; }
}
