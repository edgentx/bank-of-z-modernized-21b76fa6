package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start the reconciliation process for a specific batch window.
 */
public record StartReconciliationCmd(String batchId, String batchWindow, Instant occurredAt) implements Command {
}
