package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private Status status = Status.NONE;
    private boolean previousBatchPending = false;
    private boolean entriesMissing = false;

    public enum Status { NONE, STARTED, BALANCED, FAILED }

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
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> forceBalance(ForceBalanceCmd cmd) {
        // Validation 1: Batch Status
        if (status != Status.STARTED) {
            throw new IllegalStateException("Batch must be in STARTED state to force balance");
        }

        // Validation 2: Pending Batch (S-17 Scenario 2)
        // "A reconciliation batch cannot be executed if a previous batch is still pending."
        if (previousBatchPending) {
            throw new IllegalStateException("Cannot force balance: A previous batch is still pending.");
        }

        // Validation 3: Missing Entries (S-17 Scenario 3)
        // "All transaction entries must be accounted for during the reconciliation period."
        if (entriesMissing) {
            throw new IllegalArgumentException("Cannot force balance: Not all transaction entries are accounted for.");
        }

        // Command Validations
        if (cmd.operatorId() == null || cmd.operatorId().isBlank()) {
            throw new IllegalArgumentException("operatorId is required");
        }
        if (cmd.justification() == null || cmd.justification().isBlank()) {
            throw new IllegalArgumentException("justification is required");
        }

        // Apply state change
        var event = new ReconciliationBalancedEvent(
            this.batchId,
            cmd.operatorId(),
            cmd.justification(),
            Instant.now()
        );

        this.status = Status.BALANCED;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Test helper to simulate internal state checks.
     * In a real implementation, this might be derived from a value object
     * containing a summary of the transactions being reconciled.
     */
    public void simulatePendingConstraint(boolean isPending) {
        this.previousBatchPending = isPending;
    }

    public void simulateMissingEntries(boolean isMissing) {
        this.entriesMissing = isMissing;
    }

    // Internal state mutation (simplified for BDD)
    public void apply(ReconciliationStartedEvent event) {
        this.status = Status.STARTED;
    }

    public Status getStatus() {
        return status;
    }
}