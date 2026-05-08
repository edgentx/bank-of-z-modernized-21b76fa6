package com.example.domain.ui.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}
