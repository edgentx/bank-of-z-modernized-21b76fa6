package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Internal event used to setup the aggregate state for the ForceBalanceCmd tests.
 * In a full implementation, this would be emitted by a StartReconciliationCmd.
 */
public record ReconciliationStartedEvent(String batchId, String operatorId, Instant occurredAt) implements DomainEvent {
    public ReconciliationStartedEvent {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }
}