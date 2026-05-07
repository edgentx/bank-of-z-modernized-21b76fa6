package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a specific batch window.
 * Used in S-16.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant batchWindowStart,
        Instant batchWindowEnd
) implements Command {}
