package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a specific batch window.
 */
public record StartReconciliationCmd(String batchId, Instant start, Instant end) implements Command {
    public StartReconciliationCmd {
        if (batchId == null || batchId.isBlank()) {
            throw new IllegalArgumentException("batchId cannot be null or empty");
        }
        if (start == null) {
            throw new IllegalArgumentException("start time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("end time cannot be null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end time cannot be before start time");
        }
    }
}
