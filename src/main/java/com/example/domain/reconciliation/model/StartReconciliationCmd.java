package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to start a new reconciliation batch for a specific time window.
 */
public record StartReconciliationCmd(
        String batchId,
        Instant windowStart,
        Instant windowEnd
) implements Command {
}