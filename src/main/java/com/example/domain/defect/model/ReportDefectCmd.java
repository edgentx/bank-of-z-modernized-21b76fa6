package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., VW-454).
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl,
    String projectId
) implements Command {}