package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

/**
 * Reconciliation Batch Aggregate.
 * Handles the lifecycle of a financial reconciliation batch for a specific window.
 */
public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private BatchStatus status = BatchStatus.IDLE;
    private LocalDate currentWindow;
    
    // In a real application, this flag would be calculated based on transaction totals 
    // vs external feeds. For this domain exercise, we model it as internal state.
    private boolean accountedFor = true; 

    public enum BatchStatus { IDLE, PENDING, COMPLETED }

    public ReconciliationBatch(String batchId) {
        this.batchId = batchId;
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
        // Invariant: Cannot execute if previous batch is pending
        if (this.status == BatchStatus.PENDING) {
            throw new IllegalStateException("A reconciliation batch cannot be executed if a previous batch is still pending.");
        }

        // Invariant: All transaction entries must be accounted for
        if (!this.accountedFor) {
            throw new IllegalStateException("All transaction entries must be accounted for during the reconciliation period.");
        }

        if (cmd.batchWindow() == null) {
            throw new IllegalArgumentException("batchWindow is required");
        }

        var event = new ReconciliationStartedEvent(this.batchId, cmd.batchWindow(), Instant.now());
        
        this.status = BatchStatus.PENDING;
        this.currentWindow = cmd.batchWindow();
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Package-private helper for testing the 'unaccounted entries' scenario.
     * This simulates the state where data integrity checks have failed.
     */
    void markUnaccountedForTesting() {
        this.accountedFor = false;
    }
}
