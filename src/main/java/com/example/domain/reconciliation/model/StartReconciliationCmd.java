package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a batch.
 */
public record StartReconciliationCmd(String batchId, Instant startTime, Instant endTime) implements Command {
}
