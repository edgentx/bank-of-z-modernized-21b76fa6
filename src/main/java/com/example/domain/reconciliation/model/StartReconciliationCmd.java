package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartReconciliationCmd(String batchId, Instant batchWindow) implements Command {
    public StartReconciliationCmd {
        if (batchId == null || batchId.isBlank()) {
            throw new IllegalArgumentException("batchId cannot be null or blank");
        }
        if (batchWindow == null) {
            throw new IllegalArgumentException("batchWindow cannot be null");
        }
    }
}