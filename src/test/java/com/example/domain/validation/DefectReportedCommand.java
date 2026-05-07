package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command issued when a defect is detected by the validation pipeline.
 * Part of the S-FB-1 defect fix.
 */
public record DefectReportedCommand(
    String defectId,
    String projectId,
    String severity,
    String githubUrl
) implements Command {}
