package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a screen is successfully rendered.
 * Story: S-21
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Map<String, String> layout,
    Instant occurredAt
) implements DomainEvent {

  @Override
  public String type() {
    return "screen.rendered";
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
