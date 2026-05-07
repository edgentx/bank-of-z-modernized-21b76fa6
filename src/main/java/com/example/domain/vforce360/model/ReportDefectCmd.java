package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in the VForce360 diagnostic process.
 * Part of Story VW-454 validation.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String githubUrl,
        String severity
) implements Command {}
