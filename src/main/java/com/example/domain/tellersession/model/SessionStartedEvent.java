package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(UUID aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "tellersession.session.started"; }
  @Override public String aggregateId() { return aggregateId.toString(); }
  @Override public Instant occurredAt() { return occurredAt; }
}
