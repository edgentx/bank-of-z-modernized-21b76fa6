package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Mirrors the temporal-worker input structure.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component,
    String projectId
) implements Command {}