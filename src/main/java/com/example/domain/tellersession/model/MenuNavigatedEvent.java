package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Domain event emitted when a teller successfully navigates to a new screen.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String currentContext,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "menu.navigated";
    }

    // No explicit constructor needed for records unless validation is required.
    // This fixes the 'invalid canonical constructor' error.
}
