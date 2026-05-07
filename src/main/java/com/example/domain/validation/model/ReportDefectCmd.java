package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect identified in the VForce360 system.
 */
public record ReportDefectCmd(
    String projectId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
