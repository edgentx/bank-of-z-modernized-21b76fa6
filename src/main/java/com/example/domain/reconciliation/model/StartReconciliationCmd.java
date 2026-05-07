package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to start the reconciliation process for a batch.
 * Corresponds to Story S-16.
 */
public class StartReconciliationCmd implements Command {
    private final String batchId;
    private final Instant batchWindowStart;
    private final Instant batchWindowEnd;

    public StartReconciliationCmd(String batchId, Instant batchWindowStart, Instant batchWindowEnd) {
        this.batchId = batchId;
        this.batchWindowStart = batchWindowStart;
        this.batchWindowEnd = batchWindowEnd;
    }

    public String batchId() {
        return batchId;
    }

    public Instant batchWindowStart() {
        return batchWindowStart;
    }

    public Instant batchWindowEnd() {
        return batchWindowEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartReconciliationCmd that = (StartReconciliationCmd) o;
        return Objects.equals(batchId, that.batchId) && Objects.equals(batchWindowStart, that.batchWindowStart) && Objects.equals(batchWindowEnd, that.batchWindowEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId, batchWindowStart, batchWindowEnd);
    }
}
