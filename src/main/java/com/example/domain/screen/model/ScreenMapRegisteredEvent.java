package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record ScreenMapRegisteredEvent(
    String screenMapId, String mapName, int rows, int columns, String layoutSpec, Instant occurredAt
) implements DomainEvent {
  @Override public String type() { return "screen.map.registered"; }
  @Override public String aggregateId() { return screenMapId; }
}
