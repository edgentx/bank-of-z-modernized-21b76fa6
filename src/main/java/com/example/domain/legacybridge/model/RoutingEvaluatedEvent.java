package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a routing decision is successfully evaluated.
 * S-23: Routing evaluated event.
 */
public record RoutingEvaluatedEvent(
    String aggregateId,
    String transactionType,
    String targetSystem,
    int rulesVersion,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "routing.evaluated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
