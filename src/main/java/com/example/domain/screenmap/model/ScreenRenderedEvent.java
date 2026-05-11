package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String screenId,
    String deviceType,
    String layoutDefinition
) implements DomainEvent {
  public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String layoutDefinition) {
    this("screen.rendered", aggregateId, Instant.now(), screenId, deviceType, layoutDefinition);
  }
}
