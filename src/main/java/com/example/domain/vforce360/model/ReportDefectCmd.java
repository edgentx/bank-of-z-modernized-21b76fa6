package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger a defect report to VForce360.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String githubUrl
) implements Command {}