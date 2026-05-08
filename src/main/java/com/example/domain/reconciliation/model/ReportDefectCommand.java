package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command representing a request to report a defect.
 * Triggered by Temporal workflow exec.
 */
public record ReportDefectCommand(
    String batchId,
    String sourceSystem,
    BigDecimal discrepancyAmount,
    String reason
) implements Command {}
