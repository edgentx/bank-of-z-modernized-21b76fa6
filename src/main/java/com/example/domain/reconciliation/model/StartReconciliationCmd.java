package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to start the reconciliation process for a specific batch window.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant windowStart,
        Instant windowEnd
) implements Command {
    public StartReconciliationCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(windowStart, "windowStart cannot be null");
        Objects.requireNonNull(windowEnd, "windowEnd cannot be null");
        if (windowEnd.isBefore(windowStart)) {
            throw new IllegalArgumentException("windowEnd must be after windowStart");
        }
    }
}
