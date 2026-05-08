package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., a PM diagnostic alert).
 * Corresponds to temporal-worker exec trigger "_report_defect".
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubIssueUrl
) implements Command {}
