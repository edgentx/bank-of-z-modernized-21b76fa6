package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via the Temporal worker.
 * This triggers the validation and Slack notification workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String projectId
) implements Command {}