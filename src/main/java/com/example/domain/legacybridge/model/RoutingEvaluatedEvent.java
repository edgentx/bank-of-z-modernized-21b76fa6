package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record RoutingEvaluatedEvent(
        String aggregateId,
        String targetSystem,
        int appliedVersion,
        Map<String, Object> payload,
        Instant occurredAt
) implements DomainEvent {
    // Record component accessors (aggregateId(), occurredAt()) already satisfy
    // DomainEvent — overriding them with manual methods that called themselves
    // was an S-23 implementation typo that StackOverflowed under BDD coverage.
    @Override
    public String type() {
        return "routing.evaluated";
    }
}
