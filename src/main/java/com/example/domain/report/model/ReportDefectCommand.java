package com.example.domain.report.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to the external VForce360 system.
 * Triggered by temporal-worker exec.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String severity
) implements Command {
}