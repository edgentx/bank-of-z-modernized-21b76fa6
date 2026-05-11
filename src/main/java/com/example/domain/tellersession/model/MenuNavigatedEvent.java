package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String targetMenuId,
    String action
) implements DomainEvent {
  public MenuNavigatedEvent {
    if (type == null) type = "MenuNavigatedEvent";
    if (occurredAt == null) occurredAt = Instant.now();
  }

  public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action) {
    this("MenuNavigatedEvent", aggregateId, Instant.now(), targetMenuId, action);
  }
}
