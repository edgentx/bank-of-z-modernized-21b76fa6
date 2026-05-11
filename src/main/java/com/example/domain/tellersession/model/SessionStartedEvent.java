package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {

  @Override
  public String type() {
    return "session.started";
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }

  @Override
  public String aggregateId() {
    return aggregateId;
  }

  public SessionStartedEvent {
    Objects.requireNonNull(aggregateId, "Aggregate ID must not be null");
    Objects.requireNonNull(occurredAt, "OccurredAt must not be null");
  }
}