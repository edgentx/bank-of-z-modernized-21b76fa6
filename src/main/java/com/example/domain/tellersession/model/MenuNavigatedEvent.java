package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
  public MenuNavigatedEvent {
    if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be null/blank");
  }

  @Override public String type() { return "menu.navigated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }

  public static MenuNavigatedEvent create(String aggregateId, String menuId, String action) {
    return new MenuNavigatedEvent(aggregateId, menuId, action, Instant.now());
  }
}
