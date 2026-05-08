package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a defect is reported within the Reconciliation Batch aggregate.
 */
public record DefectReportedEvent(
    String aggregateId,
    String sourceSystem,
    BigDecimal discrepancyAmount,
    String reason,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}