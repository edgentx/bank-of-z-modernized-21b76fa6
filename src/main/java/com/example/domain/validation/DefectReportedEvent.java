package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a defect is successfully reported and validated.
 */
public record DefectReportedEvent(
        String aggregateId,
        String channel,
        String messageBody,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "DefectReportedEvent";
    }

    // Explicit canonical constructor to prevent synthetic field issues if needed,
    // though record handles this. Keeping it simple.
    public DefectReportedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }
}
