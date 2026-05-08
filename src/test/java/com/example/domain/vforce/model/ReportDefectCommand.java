package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected in the VForce360 or Mainframe system.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String severity,
    String component
) implements Command {}
