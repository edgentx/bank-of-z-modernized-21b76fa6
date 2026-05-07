package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a batch.
 * Corresponds to Story S-16.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant windowStart,
        Instant windowEnd,
        String operatorId
) implements Command {
}