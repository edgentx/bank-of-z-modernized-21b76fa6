package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., VW-454).
 * Triggers the validation workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String projectId
) implements Command {}
