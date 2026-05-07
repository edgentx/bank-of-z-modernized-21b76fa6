package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to start the reconciliation process for a specific batch window.
 */
public record StartReconciliationCmd(String batchId, Instant start, Instant end) implements Command {
    public StartReconciliationCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end cannot be before start");
        }
    }
}
