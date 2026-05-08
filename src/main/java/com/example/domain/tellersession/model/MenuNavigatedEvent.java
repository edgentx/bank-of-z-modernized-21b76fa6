package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
  public MenuNavigatedEvent {
    Objects.requireNonNull(eventId, "eventId cannot be null");
    Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
    Objects.requireNonNull(menuId, "menuId cannot be null");
    Objects.requireNonNull(action, "action cannot be null");
    Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
  }

  public static MenuNavigatedEvent create(String aggregateId, String menuId, String action) {
    return new MenuNavigatedEvent(
        UUID.randomUUID().toString(),
        aggregateId,
        menuId,
        action,
        Instant.now()
    );
  }

  @Override
  public String type() {
    return "menu.navigated";
  }

  @Override
  public String aggregateId() {
    return aggregateId;
  }
}
