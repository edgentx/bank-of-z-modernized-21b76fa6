package com.example.domain.reporting.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered during reconciliation or manual checks.
 * Corresponds to the temporal-worker exec trigger.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String githubUrl,
    String severity
) implements Command {}
