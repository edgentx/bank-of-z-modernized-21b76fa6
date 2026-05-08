package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected during reconciliation.
 * Triggered by Temporal workflow execution.
 */
public record ReportDefectCmd(
        String defectId,
        String projectName,
        String description
) implements Command {}
