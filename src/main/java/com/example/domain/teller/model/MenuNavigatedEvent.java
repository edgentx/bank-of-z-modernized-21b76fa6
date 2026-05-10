package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) implements DomainEvent {
  public MenuNavigatedEvent {
    if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
  }
  @Override public String type() { return "menu.navigated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
