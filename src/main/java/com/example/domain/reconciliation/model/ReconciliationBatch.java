package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ReconciliationBatch Aggregate
 * Handles the logic for starting and forcing balance of batches.
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

    private List<DomainEvent> startReconciliation(StartReconciliationCmd cmd) {
        // Invariant: Cannot execute if a previous batch is still pending
        if (isPreviousBatchPending) {
            throw new IllegalStateException("Cannot execute batch: Previous batch is still pending.");
        }

        // Invariant: All transaction entries must be accounted for
        if (!areAllEntriesAccounted) {
            throw new IllegalStateException("Cannot execute batch: Not all transaction entries are accounted for.");
        }

        // Validate Command fields
        if (cmd.batchWindowStart() == null || cmd.batchWindowEnd() == null) {
            throw new IllegalArgumentException("Batch window start and end are required.");
        }
        
        if (cmd.batchWindowEnd().isBefore(cmd.batchWindowStart())) {
             throw new IllegalArgumentException("Batch window end must be after start.");
        }

        var event = ReconciliationStartedEvent.create(
                this.batchId,
                cmd.batchWindowStart(),
                cmd.batchWindowEnd()
        );

        // Apply state changes (if starting implies a state change, e.g. to PROCESSING)
        // Currently status remains OPEN or moves to a processing state.
        // For this story, we assume status stays OPEN or moves implicitly.
        // We will assume status remains OPEN as per existing code structure, 
        // or we could add a PROCESSING status. Given existing enum has OPEN/BALANCED/CLOSED,
        // we will leave it as OPEN, but the event marks the logical start.
        
        addEvent(event);
        incrementVersion();

        return List.of(event);
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
