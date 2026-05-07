package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record StartReconciliationCmd(String batchId, Instant batchWindow) implements Command {
    public StartReconciliationCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(batchWindow, "batchWindow cannot be null");
    }
}
