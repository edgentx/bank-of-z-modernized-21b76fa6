package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect is reported in the VForce360 system.
 * Ideally immutable, following the DomainEvent contract.
 */
public record DefectReportedEvent(
        String aggregateId, // Project ID or similar aggregate identifier
        String type,
        Instant occurredAt,
        String defectId,
        String title,
        String description,
        String reporter,
        String severity
) implements DomainEvent {
    public DefectReportedEvent(String aggregateId, String title, String description, String reporter, String severity) {
        this(
                aggregateId,
                "DefectReportedEvent",
                Instant.now(),
                UUID.randomUUID().toString(),
                title,
                description,
                reporter,
                severity
        );
    }
}