package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a reconciliation batch is started.
 * Corresponds to Story S-16.
 */
public class ReconciliationStartedEvent implements DomainEvent {

    private final String batchId;
    private final String type;
    private final Instant batchWindowStart;
    private final Instant batchWindowEnd;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String batchId, Instant batchWindowStart, Instant batchWindowEnd, Instant occurredAt) {
        this.batchId = batchId;
        this.type = "ReconciliationStarted";
        this.batchWindowStart = batchWindowStart;
        this.batchWindowEnd = batchWindowEnd;
        this.occurredAt = occurredAt;
    }

    // Full constructor preserving potential support for a user-provided type field
    public ReconciliationStartedEvent(String batchId, String type, Instant batchWindowStart, Instant batchWindowEnd, Instant occurredAt) {
        this.batchId = batchId;
        this.type = type;
        this.batchWindowStart = batchWindowStart;
        this.batchWindowEnd = batchWindowEnd;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return batchId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant getBatchWindowStart() {
        return batchWindowStart;
    }

    public Instant getBatchWindowEnd() {
        return batchWindowEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationStartedEvent that = (ReconciliationStartedEvent) o;
        return Objects.equals(batchId, that.batchId) && Objects.equals(type, that.type) && Objects.equals(batchWindowStart, that.batchWindowStart) && Objects.equals(batchWindowEnd, that.batchWindowEnd) && Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId, type, batchWindowStart, batchWindowEnd, occurredAt);
    }
}
