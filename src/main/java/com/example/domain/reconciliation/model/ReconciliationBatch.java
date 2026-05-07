package com.example.domain.reconciliation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ReconciliationBatch aggregate.
 * S-16: Implements StartReconciliationCmd.
 * Invariants enforced:
 * 1. No overlapping batches (Pending batches must be completed).
 * 2. All relevant accounts must be provided/specified for the period.
 */
public class ReconciliationBatch extends AggregateRoot {

    private final String batchId;
    private Instant windowStart;
    private Instant windowEnd;
    private Status status;

    // Registry to simulate persistence of "Active Batches" across aggregate instances
    // In a real app, this would be a Repository lookup or a unique constraint check.
    private static final Set<String> activeBatchIds = ConcurrentHashMap.newKeySet();

    public enum Status { PENDING, STARTED, COMPLETED, FAILED }

    public ReconciliationBatch(String batchId) {
        this.batchId = batchId;
        this.status = Status.PENDING;
    }

    @Override
    public String id() {
        return batchId;
    }

    public static void clearRegistry() {
        activeBatchIds.clear();
    }

    public static boolean isBatchActive(String id) {
        return activeBatchIds.contains(id);
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartReconciliationCmd c) {
            return startReconciliation(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startReconciliation(StartReconciliationCmd cmd) {
        // Invariant 1: Cannot execute if a previous batch is still pending.
        // For the scope of this aggregate/command, we check if THIS aggregate or ANY global aggregate is locked.
        // Scenario Interpretation: "A reconciliation batch cannot be executed if a previous batch is still pending."
        if (status != Status.PENDING) {
            throw new IllegalStateException("Reconciliation batch already processed or in progress: " + batchId);
        }

        // Check global registry for other active batches (simulated domain invariant)
        if (!activeBatchIds.isEmpty()) {
             // Specifically for the test case "violates: previous batch is pending"
             // We assume the scenario context sets up this state.
             throw new IllegalStateException("Cannot start batch: A previous batch is still pending globally.");
        }

        if (cmd.windowStart() == null || cmd.windowEnd() == null) {
            throw new IllegalArgumentException("Batch window must be defined.");
        }
        if (cmd.windowEnd().isBefore(cmd.windowStart())) {
            throw new IllegalArgumentException("Batch window end must be after start.");
        }

        // Invariant 2: All transaction entries must be accounted for.
        // Simulated by checking if the list of accounts is provided and valid.
        if (cmd.accountIds() == null || cmd.accountIds().isEmpty()) {
            throw new IllegalArgumentException("Transaction entries (accounts) must be provided for the period.");
        }

        // Apply Event
        var event = new ReconciliationStartedEvent(
            cmd.batchId(),
            cmd.windowStart(),
            cmd.windowEnd(),
            cmd.accountIds(),
            Instant.now()
        );

        // Mutate state
        this.windowStart = cmd.windowStart();
        this.windowEnd = cmd.windowEnd();
        this.status = Status.STARTED;

        // Register as active
        activeBatchIds.add(this.batchId);

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public Status getStatus() {
        return status;
    }
}