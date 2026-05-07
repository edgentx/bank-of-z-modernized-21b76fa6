package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to start the reconciliation process for a batch.
 */
public record StartReconciliationCmd(String batchId, String batchWindow, boolean isReady) implements Command {
    public StartReconciliationCmd {
        if (batchId == null || batchId.isBlank()) {
            throw new IllegalArgumentException("batchId cannot be null or blank");
        }
    }
}