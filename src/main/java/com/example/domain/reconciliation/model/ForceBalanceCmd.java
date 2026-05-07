package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to force a ReconciliationBatch to a balanced state.
 */
public record ForceBalanceCmd(String batchId, String operatorId, String justification) implements Command {
    public ForceBalanceCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(operatorId, "operatorId cannot be null");
        Objects.requireNonNull(justification, "justification cannot be null");
        if (batchId.isBlank()) throw new IllegalArgumentException("batchId cannot be blank");
        if (operatorId.isBlank()) throw new IllegalArgumentException("operatorId cannot be blank");
        if (justification.isBlank()) throw new IllegalArgumentException("justification cannot be blank");
    }
}