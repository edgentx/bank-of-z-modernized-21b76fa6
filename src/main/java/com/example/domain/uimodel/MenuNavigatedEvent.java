package com.example.domain.uimodel;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
