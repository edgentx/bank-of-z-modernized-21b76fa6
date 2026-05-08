package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}
