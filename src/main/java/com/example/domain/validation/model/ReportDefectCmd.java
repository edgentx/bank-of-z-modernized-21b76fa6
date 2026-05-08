package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect identified in the VForce360 system.
 * Used as input to the ReportDefectAggregate.
 */
public record ReportDefectCmd(
    String defectId,
    String storyId,
    String title,
    String severity,
    String component
) implements Command {}
