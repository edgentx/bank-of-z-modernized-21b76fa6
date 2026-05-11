package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

public record MenuNavigatedEvent(
    String aggregateId,
    String sessionId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
  public MenuNavigatedEvent {
    Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
    Objects.requireNonNull(sessionId, "sessionId cannot be null");
    Objects.requireNonNull(targetMenuId, "targetMenuId cannot be null");
    Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
  }

  @Override
  public String type() {
    return "menu.navigated";
  }
}