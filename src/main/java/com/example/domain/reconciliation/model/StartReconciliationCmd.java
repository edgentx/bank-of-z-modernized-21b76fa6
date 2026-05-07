package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the batch reconciliation process for a given period.
 */
public record StartReconciliationCmd(String batchId, Instant windowStart, Instant windowEnd) implements Command {
}
