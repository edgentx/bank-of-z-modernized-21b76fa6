package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    Instant occurredAt
) implements DomainEvent {}
