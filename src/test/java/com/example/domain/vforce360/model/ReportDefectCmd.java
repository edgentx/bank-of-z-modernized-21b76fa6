package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected by VForce360.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId
) implements Command {}