package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Set;

/**
 * Event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> roles,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "tellersession.started"; }
  @Override public String aggregateId() { return sessionId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
