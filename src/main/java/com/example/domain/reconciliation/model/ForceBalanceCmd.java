package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to force a batch to a balanced state, typically used by operators to resolve discrepancies.
 */
public record ForceBalanceCmd(
        String batchId,
        String operatorId,
        String justification
) implements Command {
    public ForceBalanceCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(operatorId, "operatorId cannot be null");
        Objects.requireNonNull(justification, "justification cannot be null");
    }
}
