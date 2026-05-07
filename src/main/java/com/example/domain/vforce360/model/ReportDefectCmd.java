package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCmd(
    String validationId,
    String description,
    String reporter,
    String severity
) implements Command {}
