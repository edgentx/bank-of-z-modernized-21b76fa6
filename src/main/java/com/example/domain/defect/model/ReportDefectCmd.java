package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in the system.
 * Maps to the Temporal trigger 'report_defect'.
 */
public record ReportDefectCmd(
    String defectId,
    String githubIssueUrl,
    String description
) implements Command {}
