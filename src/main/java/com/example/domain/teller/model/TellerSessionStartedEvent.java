package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record TellerSessionStartedEvent(
    String sessionId, String tellerId, String branchId, Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "teller.session.started"; }
  @Override public String aggregateId() { return sessionId; }
}
