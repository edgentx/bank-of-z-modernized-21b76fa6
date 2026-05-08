package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String eventId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

  public MenuNavigatedEvent(String sessionId, String menuId, String action, Instant occurredAt) {
    this(UUID.randomUUID().toString(), sessionId, menuId, action, occurredAt);
  }

  @Override public String type() { return "menu.navigated"; }
  @Override public String aggregateId() { return sessionId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
