package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ReconciliationBatch aggregate.
 * Manages the batch reconciliation process for a given period.
 */
public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private String status; // IDLE, PENDING, IN_PROGRESS, COMPLETED
    private Instant startedAt;
    private Instant completedAt;

    public ReconciliationBatch(String batchId) {
        this.batchId = batchId;
        this.status = "IDLE";
    }

    @Override
    public String id() {
        return batchId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartReconciliationCmd c) {
            return startReconciliation(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startReconciliation(StartReconciliationCmd cmd) {
        // Invariant: A reconciliation batch cannot be executed if a previous batch is still pending.
        if ("PENDING".equals(this.status) || "IN_PROGRESS".equals(this.status)) {
            throw new IllegalStateException("Cannot start reconciliation: previous batch is still pending or in progress.");
        }

        // Invariant: All transaction entries must be accounted for during the reconciliation period.
        // Assuming the command contains a flag or checksum indicating readiness.
        if (cmd == null || !cmd.isReady()) {
            throw new IllegalArgumentException("Cannot start reconciliation: transaction entries are not fully accounted for.");
        }

        if (cmd.batchWindow() == null || cmd.batchWindow().isBlank()) {
            throw new IllegalArgumentException("Batch window must be provided");
        }

        ReconciliationStartedEvent event = new ReconciliationStartedEvent(
            this.batchId,
            cmd.batchWindow(),
            Instant.now()
        );

        this.status = "IN_PROGRESS";
        this.startedAt = event.occurredAt();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public String getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}