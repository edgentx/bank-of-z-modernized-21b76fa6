package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start a reconciliation batch.
 */
public record StartReconciliationCmd(
    String batchId,
    String batchWindow,
    Instant periodStart,
    Instant periodEnd,
    boolean entriesAccountedFor // Simulated check result from domain service or infrastructure
) implements Command {}
