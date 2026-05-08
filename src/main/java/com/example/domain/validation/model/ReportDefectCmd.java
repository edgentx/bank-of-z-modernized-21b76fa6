package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via the VForce360 workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String severity,
    String component
) implements Command {}
