package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.LocalDate;

/**
 * Command to start a new reconciliation batch.
 * @param batchId The ID of the aggregate.
 * @param batchWindow The date window for the reconciliation.
 */
public record StartReconciliationCmd(String batchId, LocalDate batchWindow) implements Command {}
