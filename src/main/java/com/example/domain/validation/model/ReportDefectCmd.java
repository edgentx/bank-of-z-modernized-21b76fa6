package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via VForce360 temporal workflow.
 * Corresponds to S-FB-1: Trigger _report_defect via temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}