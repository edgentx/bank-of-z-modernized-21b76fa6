package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to start the reconciliation process for a batch.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant windowStart,
        Instant windowEnd
) implements Command {
    public StartReconciliationCmd {
        Objects.requireNonNull(batchId, "batchId is required");
        Objects.requireNonNull(windowStart, "windowStart is required");
        Objects.requireNonNull(windowEnd, "windowEnd is required");
        if (windowEnd.isBefore(windowStart)) {
            throw new IllegalArgumentException("windowEnd must be after windowStart");
        }
    }
}