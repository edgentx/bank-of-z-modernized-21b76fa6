package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

public class MenuNavigatedEvent implements DomainEvent {
  private final String aggregateId;
  private final String menuId;
  private final String action;
  private final Instant occurredAt;

  public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
    this.aggregateId = Objects.requireNonNull(aggregateId);
    this.menuId = Objects.requireNonNull(menuId);
    this.action = Objects.requireNonNull(action);
    this.occurredAt = Objects.requireNonNull(occurredAt);
  }

  @Override public String type() { return "menu.navigated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }

  public String getMenuId() { return menuId; }
  public String getAction() { return action; }
}