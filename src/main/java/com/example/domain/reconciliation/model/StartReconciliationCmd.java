package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartReconciliationCmd(
        String batchId,
        Instant windowStart,
        Instant windowEnd
) implements Command {
    public StartReconciliationCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(windowStart, "windowStart cannot be null");
        Objects.requireNonNull(windowEnd, "windowEnd cannot be null");
    }
}
