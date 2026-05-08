package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to the VForce360 system via temporal-worker.
 */
public record ReportDefectCmd(
    String defectId,
    String severity,
    String component,
    String projectId,
    String description
) implements Command {}
