package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., via VForce360).
 * Triggers validation and GitHub/Slack workflows.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String projectId
) implements Command {}
