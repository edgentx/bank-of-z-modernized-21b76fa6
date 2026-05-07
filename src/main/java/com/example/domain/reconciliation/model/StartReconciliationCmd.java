package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a specific batch window.
 * @param batchId The unique identifier of the batch.
 * @param batchWindowStart The start time of the reconciliation window.
 * @param batchWindowEnd The end time of the reconciliation window.
 * @param operatorId The ID of the operator initiating the reconciliation.
 */
public record StartReconciliationCmd(String batchId, Instant batchWindowStart, Instant batchWindowEnd, String operatorId) implements Command {
}
