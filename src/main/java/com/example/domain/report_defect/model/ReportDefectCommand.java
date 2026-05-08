package com.example.domain.report_defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered via Temporal workflow or API.
 */
public record ReportDefectCommand(
    String defectId,
    String description,
    Severity severity,
    String component
) implements Command {}
