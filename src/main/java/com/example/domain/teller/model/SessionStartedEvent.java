package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Set;

/**
 * Event emitted when a teller session is successfully started.
 * S-18: user-interface-navigation
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Set<String> permissions,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "teller.session.started";
    }
}
