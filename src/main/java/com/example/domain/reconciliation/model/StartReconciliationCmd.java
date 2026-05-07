package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartReconciliationCmd(String batchId, Instant batchWindowStart, Instant batchWindowEnd) implements Command {
    public StartReconciliationCmd {
        if (batchId == null || batchId.isBlank()) {
            throw new IllegalArgumentException("batchId cannot be null or blank");
        }
        if (batchWindowStart == null) {
            throw new IllegalArgumentException("batchWindowStart cannot be null");
        }
        if (batchWindowEnd == null) {
            throw new IllegalArgumentException("batchWindowEnd cannot be null");
        }
        if (batchWindowEnd.isBefore(batchWindowStart)) {
            throw new IllegalArgumentException("batchWindowEnd must be after batchWindowStart");
        }
    }
}