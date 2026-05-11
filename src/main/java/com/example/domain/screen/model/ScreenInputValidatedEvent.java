package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when screen input has been successfully validated against map rules.
 */
public record ScreenInputValidatedEvent(String aggregateId, String screenId, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "input.validated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}