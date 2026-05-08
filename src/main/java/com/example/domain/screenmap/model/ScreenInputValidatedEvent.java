package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input is successfully validated.
 */
public record ScreenInputValidatedEvent(
  String aggregateId,
  String screenMapId,
  Map<String, String> validatedFields,
  Instant occurredAt
) implements DomainEvent {
  @Override
  public String type() {
    return "screen.input.validated";
  }

  @Override
  public String aggregateId() {
    return aggregateId();
  }

  @Override
  public Instant occurredAt() {
    return occurredAt();
  }
}
