package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when screen input has been successfully validated.
 */
public record InputValidatedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String screenId,
    Map<String, String> inputFields
) implements DomainEvent {
  public InputValidatedEvent(String aggregateId, String screenId, Map<String, String> inputFields) {
    this("input.validated", aggregateId, Instant.now(), screenId, inputFields);
  }
}