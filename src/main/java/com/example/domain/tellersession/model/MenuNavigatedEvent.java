package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu context.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "menu.navigated"; }
  @Override public String aggregateId() { return aggregateId; }
  @Override public Instant occurredAt() { return occurredAt; }
}
