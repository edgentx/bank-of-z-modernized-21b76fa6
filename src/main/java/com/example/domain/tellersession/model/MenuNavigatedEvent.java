package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record MenuNavigatedEvent(
  String type,
  String aggregateId,
  Instant occurredAt,
  String sessionId,
  String menuId,
  String action
) implements DomainEvent {
  public MenuNavigatedEvent {
    if (type == null) type = "menu.navigated";
  }

  public static MenuNavigatedEvent create(String aggregateId, String sessionId, String menuId, String action) {
    return new MenuNavigatedEvent("menu.navigated", aggregateId, Instant.now(), sessionId, menuId, action);
  }
}
