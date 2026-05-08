package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected by the VForce360 diagnostic system.
 * Immutable value object.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String projectUuid,
    String storyId,
    String reproductionSteps
) implements Command {}
