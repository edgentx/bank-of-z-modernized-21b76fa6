package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate the reconciliation process for a specific batch window.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant batchWindowStart,
        Instant batchWindowEnd,
        String operatorId
) implements Command {
}