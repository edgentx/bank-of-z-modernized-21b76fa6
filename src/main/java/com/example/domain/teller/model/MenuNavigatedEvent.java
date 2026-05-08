package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
