package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Corresponds to the trigger in the VW-454 reproduction steps.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}
