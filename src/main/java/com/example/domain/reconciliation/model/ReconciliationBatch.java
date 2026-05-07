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
    private boolean balanced = false;
    private boolean previousBatchPending = false;
    private boolean entriesAccountedFor = false;

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
        // Invariant: A reconciliation batch cannot be executed if a previous batch is still pending.
        if (previousBatchPending) {
            throw new IllegalStateException("Cannot force balance: previous batch is still pending.");
        }

        // Invariant: All transaction entries must be accounted for during the reconciliation period.
        if (!entriesAccountedFor) {
            throw new IllegalStateException("Cannot force balance: transaction entries are not accounted for.");
        }

        if (balanced) {
            throw new IllegalStateException("Batch is already balanced.");
        }

        var event = new ReconciliationBalancedEvent(batchId, cmd.operatorId(), cmd.justification(), Instant.now());
        this.balanced = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // State setters for testing
    public void setPreviousBatchPending(boolean pending) {
        this.previousBatchPending = pending;
    }

    public void setEntriesAccountedFor(boolean accounted) {
        this.entriesAccountedFor = accounted;
    }
    
    public boolean isBalanced() {
        return balanced;
    }
}