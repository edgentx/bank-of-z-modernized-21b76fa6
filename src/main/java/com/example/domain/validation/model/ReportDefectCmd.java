package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Encapsulates the necessary information to trigger the external reporting workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
