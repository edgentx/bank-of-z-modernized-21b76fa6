package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record ScreenRenderedEvent(
  String type,
  String aggregateId,
  Instant occurredAt,
  String screenId,
  String deviceType
) implements DomainEvent {
  public ScreenRenderedEvent {
    if (type == null) type = "screen.rendered";
  }

  public static ScreenRenderedEvent create(String aggregateId, String screenId, String deviceType) {
    return new ScreenRenderedEvent("screen.rendered", aggregateId, Instant.now(), screenId, deviceType);
  }
}
