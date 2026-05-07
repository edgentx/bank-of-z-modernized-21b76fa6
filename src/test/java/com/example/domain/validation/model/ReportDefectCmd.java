package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Part of the Validation Domain context.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String severity
) implements Command {}
