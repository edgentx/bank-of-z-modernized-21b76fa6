package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected by the VForce360 PM diagnostic tool.
 * Validates inputs before creating the DefectReportedEvent.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String description
) implements Command {}
