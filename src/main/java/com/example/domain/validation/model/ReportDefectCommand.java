package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used by the Temporal workflow to trigger domain logic.
 */
public record ReportDefectCommand(
    String defectId,
    String summary,
    String githubUrl
) implements Command {}
