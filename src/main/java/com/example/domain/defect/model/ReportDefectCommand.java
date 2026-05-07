package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to the VForce360 system.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
