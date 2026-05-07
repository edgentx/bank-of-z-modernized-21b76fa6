package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ReconciliationBatch Aggregate
 * Handles the logic for forcing a batch to a balanced state.
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
        // Invariant: A reconciliation batch cannot be executed if a previous batch is still pending.
        if (isPreviousBatchPending) {
            throw new IllegalStateException("Cannot execute batch: Previous batch is still pending.");
        }

        // Invariant: All transaction entries must be accounted for during the reconciliation period.
        if (!areAllEntriesAccounted) {
            throw new IllegalStateException("Cannot execute batch: Not all transaction entries are accounted for.");
        }

        // Invariant: Batch must be Open to start
        if (status != Status.OPEN) {
            throw new IllegalStateException("Cannot start reconciliation on a batch that is not OPEN.");
        }

        // Validate Command fields
        if (cmd.batchWindowStart() == null || cmd.batchWindowEnd() == null) {
            throw new IllegalArgumentException("Batch window start and end are required.");
        }

        if (cmd.batchWindowEnd().isBefore(cmd.batchWindowStart())) {
            throw new IllegalArgumentException("Batch window end must be after start.");
        }

        var event = new ReconciliationStartedEvent(
                this.batchId,
                cmd.batchWindowStart(),
                cmd.batchWindowEnd(),
                Instant.now()
        );

        // State transition implies process started. We could add a STARTED status, but keeping it simple.
        // Assuming this triggers the workflow, the batch might stay OPEN until Balanced.
        // Or status becomes RECONCILING. Based on existing enum, let's stick to OPEN or just leave as is since ForceBalance is the terminal state.
        // Given the prompt says "Apply resulting events", we just emit.
        // However, typically a state change prevents double execution.
        // Let's assume status = RECONCILING if we add it, but sticking to existing enum for safety.
        // The prompt says "Apply resulting events and enforce invariants".
        // We will assume the batch remains OPEN but potentially flag it if needed, or just emit the event.
        // S-10 pattern: apply state changes.
        // Let's add a check to ensure we don't start if already balanced? Covered by status != OPEN.

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
