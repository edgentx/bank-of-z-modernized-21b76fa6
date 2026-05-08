package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen is successfully rendered.
 * Part of Story S-21.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    Instant occurredAt,
    String deviceType,
    String status,
    Map<String, Object> layout
) implements DomainEvent {}
