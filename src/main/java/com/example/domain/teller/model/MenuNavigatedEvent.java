package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
  public MenuNavigatedEvent {
    // Ensure defaults if necessary, though record handles initialization
  }

  @Override
  public String type() {
    return "menu.navigated";
  }

  @Override
  public String aggregateId() {
    return aggregateId;
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }
}