package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454).
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String projectId,
    String title,
    String description,
    String severity
) implements Command {}
