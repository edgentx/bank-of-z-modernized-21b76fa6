package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected in the VForce360 system.
 * Triggered via Temporal workflow execution.
 */
public record ReportDefectCmd(
    String defectId,
    String projectId,
    String title,
    String severity,
    String component
) implements Command {}