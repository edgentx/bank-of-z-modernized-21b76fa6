package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.List;

public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    List<String> renderedFields,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
    
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
    
    @Override
    public String aggregateId() {
        return aggregateId;
    }
}