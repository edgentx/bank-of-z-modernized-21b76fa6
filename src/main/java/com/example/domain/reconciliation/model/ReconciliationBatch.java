package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ReconciliationBatch Aggregate
 * Handles the logic for forcing a batch to a balanced state and starting reconciliation.
 */
public class ReconciliationBatch extends AggregateRoot {
    private final String batchId;
    private Status status = Status.OPEN;
    private boolean isPreviousBatchPending = false;
    private boolean areAllEntriesAccounted = true;

    public enum Status {
        OPEN, BALANCED, CLOSED
    }

    public ReconciliationBatch(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String id() {
        return batchId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ForceBalanceCmd c) {
            return forceBalance(c);
        }
        if (cmd instanceof StartReconciliationCmd c) {
            return startReconciliation(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> forceBalance(ForceBalanceCmd cmd) {
        // Invariant: Cannot execute if a previous batch is still pending
        if (isPreviousBatchPending) {
            throw new IllegalStateException("Cannot execute batch: Previous batch is still pending.");
        }

        // Invariant: All transaction entries must be accounted for
        if (!areAllEntriesAccounted) {
            throw new IllegalStateException("Cannot execute batch: Not all transaction entries are accounted for.");
        }

        // Invariant: Only Open batches can be forced to balance
        if (status != Status.OPEN) {
            throw new IllegalStateException("Cannot force balance on a batch that is not OPEN.");
        }

        // Validate Command fields
        if (cmd.justification() == null || cmd.justification().isBlank()) {
            throw new IllegalArgumentException("Justification is required to force balance.");
        }

        var event = new ReconciliationBalancedEvent(
                this.batchId,
                cmd.operatorId(),
                cmd.justification(),
                Instant.now()
        );

        // Apply state changes
        this.status = Status.BALANCED;
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    private List<DomainEvent> startReconciliation(StartReconciliationCmd cmd) {
        // S-16 Scenario 2: A reconciliation batch cannot be executed if a previous batch is still pending.
        if (isPreviousBatchPending) {
            throw new IllegalStateException("Cannot start reconciliation: Previous batch is still pending.");
        }

        // S-16 Scenario 3: All transaction entries must be accounted for during the reconciliation period.
        if (!areAllEntriesAccounted) {
            throw new IllegalStateException("Cannot start reconciliation: Not all transaction entries are accounted for.");
        }

        // S-16 Scenario 1: Valid command execution
        if (cmd.batchId() == null || cmd.batchId().isBlank()) {
             throw new IllegalArgumentException("Batch ID is required.");
        }
        if (cmd.windowStart() == null || cmd.windowEnd() == null) {
             throw new IllegalArgumentException("Batch window must be defined.");
        }
        if (cmd.windowEnd().isBefore(cmd.windowStart())) {
             throw new IllegalArgumentException("Batch window end must be after start.");
        }

        var event = new ReconciliationStartedEvent(
                cmd.batchId(),
                cmd.windowStart(),
                cmd.windowEnd(),
                cmd.operatorId(),
                Instant.now()
        );

        // Apply state changes
        this.status = Status.OPEN; // Remains open or moves to IN_PROGRESS depending on domain requirements. Keeping OPEN for now.
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Setters for test setup (simulating loading from history)
    public void markPreviousBatchPending(boolean isPending) {
        this.isPreviousBatchPending = isPending;
    }

    public void markEntriesUnaccounted() {
        this.areAllEntriesAccounted = false;
    }

    public Status getStatus() {
        return status;
    }
}
